package cb.empty.cyberly.accounts.app;

import cb.empty.cyberly.accounts.api.dto.UserResponse;
import cb.empty.cyberly.accounts.api.dto.LoginRequest;
import cb.empty.cyberly.accounts.api.dto.RegisterRequest;
import cb.empty.cyberly.accounts.domain.User;
import cb.empty.cyberly.accounts.domain.enums.Status;
import cb.empty.cyberly.accounts.infra.UserRepository;
import cb.empty.cyberly.activity.app.LoginEventService;
import cb.empty.cyberly.activity.domain.LoginEvent;
import cb.empty.cyberly.activity.domain.enums.LoginEventType;
import cb.empty.cyberly.common.config.GeoIpService;
import cb.empty.cyberly.common.security.JwtService;
import cb.empty.cyberly.risk.app.RiskService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LoginEventService loginEventService;
    private final RiskService riskService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final GeoIpService geoIpService;

    @Value("${app.max-failed-attempts:5}")
    private int maxFailedAttempts;

    public UserResponse getMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));
        return new UserResponse(null, user.getId(), user.getEmail(), user.getStatus(), "OK");
    }

    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(Status.ACTIVE);
        userRepository.save(user);
    }

    public UserResponse login(LoginRequest request, HttpServletRequest httpRequest) {

        String ipAddress = httpRequest.getRemoteAddr();
        String device    = httpRequest.getHeader("User-Agent");
        String country   = geoIpService.getCountry(ipAddress);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid credentials"
                ));

        // Check block BEFORE password — locked users get 423 immediately
        if (user.getStatus() == Status.BLOCKED) {
            throw new ResponseStatusException(HttpStatus.LOCKED,
                    "Account is locked. Contact support.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            int attempts = user.getFailedAttempts() + 1;
            user.setFailedAttempts(attempts);

            if (attempts >= maxFailedAttempts) {
                user.setStatus(Status.BLOCKED);
                userRepository.save(user);
                loginEventService.recordEvent(user, LoginEventType.LOGIN_FAILED, ipAddress, country, device);
                throw new ResponseStatusException(HttpStatus.LOCKED,
                        "Account locked after " + maxFailedAttempts + " failed attempts.");
            }

            userRepository.save(user);
            loginEventService.recordEvent(user, LoginEventType.LOGIN_FAILED, ipAddress, country, device);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Invalid credentials. Attempts: " + attempts + "/" + maxFailedAttempts);
        }

        // Successful login — reset counter
        user.setFailedAttempts(0);
        userRepository.save(user);

        LoginEvent event = loginEventService.recordEvent(
                user, LoginEventType.LOGIN_SUCCESS, ipAddress, country, device);

        riskService.calculateRisk(user, event);

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        return new UserResponse(token, user.getId(), user.getEmail(), user.getStatus(), "Login successful");
    }
}
