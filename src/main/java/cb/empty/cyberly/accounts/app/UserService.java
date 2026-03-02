package cb.empty.cyberly.accounts.app;

import cb.empty.cyberly.accounts.api.dto.UserResponse;
import cb.empty.cyberly.accounts.api.dto.LoginRequest;
import cb.empty.cyberly.accounts.api.dto.RegisterRequest;
import cb.empty.cyberly.accounts.domain.User;
import cb.empty.cyberly.accounts.domain.Status;
import cb.empty.cyberly.accounts.infra.UserRepository;
import cb.empty.cyberly.activity.domain.LoginEvent;
import cb.empty.cyberly.activity.domain.LoginEventType;
import cb.empty.cyberly.activity.infra.LoginEventRepository;
import cb.empty.cyberly.risk.app.RiskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final LoginEventRepository loginEventRepository;
    private final RiskService riskService;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email already exists"
            );
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(Status.ACTIVE);

        userRepository.save(user);
    }

    public UserResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid credentials"
                ));

        if (user.getStatus() == Status.BLOCKED) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "User is blocked"
            );
        }

        boolean success = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        LoginEvent event = new LoginEvent();
        event.setUser(user);
        event.setIpAddress(request.getIpAddress());
        event.setCountry(request.getCountry());
        event.setDevice(request.getDevice());
        event.setType(success ? LoginEventType.LOGIN_SUCCESS
                : LoginEventType.LOGIN_FAILED);

        loginEventRepository.save(event);

        riskService.calculateRisk(user, event);

        if (!success) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid credentials"
            );
        }

        if (user.getStatus() == Status.BLOCKED) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "User blocked due to high risk"
            );
        }

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getStatus(),
                "Login successful"
        );
    }
}
