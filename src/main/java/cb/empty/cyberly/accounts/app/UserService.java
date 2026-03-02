package cb.empty.cyberly.accounts.app;

import cb.empty.cyberly.accounts.api.dto.UserResponse;
import cb.empty.cyberly.accounts.api.dto.LoginRequest;
import cb.empty.cyberly.accounts.api.dto.RegisterRequest;
import cb.empty.cyberly.accounts.domain.User;
import cb.empty.cyberly.accounts.domain.emuns.Status;
import cb.empty.cyberly.accounts.infra.UserRepository;
import cb.empty.cyberly.activity.app.LoginEventService;
import cb.empty.cyberly.activity.domain.LoginEvent;
import cb.empty.cyberly.activity.domain.enums.LoginEventType;
import cb.empty.cyberly.activity.infra.LoginEventRepository;
import cb.empty.cyberly.risk.app.RiskService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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

    public UserResponse login(LoginRequest request, HttpServletRequest httpRequest) {

        String ipAddress = httpRequest.getRemoteAddr();
        String device = httpRequest.getHeader("User-Agent");
        String country = "Unknown";

        // 1️⃣ Найти пользователя
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid credentials"
                ));

        // 2️⃣ Проверить, не заблокирован ли пользователь
        if (user.getStatus() == Status.BLOCKED) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "User is blocked"
            );
        }

        // 3️⃣ Проверить пароль
        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        // 4️⃣ Определить тип события
        LoginEventType eventType = passwordMatches
                ? LoginEventType.LOGIN_SUCCESS
                : LoginEventType.LOGIN_FAILED;

        // 5️⃣ Записать событие логина
        LoginEvent event = loginEventService.recordEvent(
                user,
                eventType,
                ipAddress,
                country,
                device
        );

        // 6️⃣ Рассчитать риск
        riskService.calculateRisk(user, event);

        // 7️⃣ Если пароль неверный — ошибка
        if (!passwordMatches) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid credentials"
            );
        }

        // 8️⃣ Проверить, не был ли пользователь заблокирован после расчёта риска
        if (user.getStatus() == Status.BLOCKED) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "User blocked due to high risk"
            );
        }

        // 9️⃣ Вернуть успешный ответ
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getStatus(),
                "Login successful"
        );
    }
}
