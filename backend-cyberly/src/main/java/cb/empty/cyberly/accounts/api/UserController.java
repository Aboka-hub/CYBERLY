package cb.empty.cyberly.accounts.api;

import cb.empty.cyberly.accounts.api.dto.UserResponse;
import cb.empty.cyberly.accounts.api.dto.LoginRequest;
import cb.empty.cyberly.accounts.app.UserService;
import cb.empty.cyberly.accounts.api.dto.RegisterRequest;
import cb.empty.cyberly.common.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Аутентификация и управление пользователями")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Регистрация", description = "Создаёт нового пользователя. Email должен быть уникальным.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Пользователь зарегистрирован"),
        @ApiResponse(responseCode = "409", description = "Email уже используется",
            content = @Content(schema = @Schema(example = "Email already exists")))
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.status(201).body("User registered successfully");
    }

    @Operation(
        summary = "Вход",
        description = """
            Аутентификация по email и паролю. Возвращает JWT-токен.

            **Блокировка аккаунта:** после **5 неудачных попыток** подряд аккаунт автоматически
            блокируется (статус BLOCKED). Счётчик сбрасывается при успешном входе.
            Количество попыток настраивается через `MAX_FAILED_ATTEMPTS` (по умолчанию: 5).
            """
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Успешный вход — возвращает JWT токен",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "401", description = "Неверный пароль. Тело: 'Invalid credentials. Attempts: N/5'",
            content = @Content(schema = @Schema(example = "Invalid credentials. Attempts: 3/5"))),
        @ApiResponse(responseCode = "423", description = "Аккаунт заблокирован (превышен лимит попыток или ранее заблокирован)",
            content = @Content(schema = @Schema(example = "Account locked after 5 failed attempts."))),
        @ApiResponse(responseCode = "404", description = "Пользователь не найден",
            content = @Content(schema = @Schema(example = "Invalid credentials")))
    })
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(userService.login(request, httpRequest));
    }

    @Operation(summary = "Текущий пользователь", description = "Возвращает профиль аутентифицированного пользователя.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Профиль пользователя",
            content = @Content(schema = @Schema(implementation = UserResponse.class))),
        @ApiResponse(responseCode = "401", description = "Токен не передан или невалиден")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        Long userId = SecurityUtils.getAuthenticatedUserId();
        return ResponseEntity.ok(userService.getMe(userId));
    }
}
