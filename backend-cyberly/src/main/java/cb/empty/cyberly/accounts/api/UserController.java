package cb.empty.cyberly.accounts.api;

import cb.empty.cyberly.accounts.api.dto.UserResponse;
import cb.empty.cyberly.accounts.api.dto.LoginRequest;
import cb.empty.cyberly.accounts.app.UserService;
import cb.empty.cyberly.accounts.api.dto.RegisterRequest;
import cb.empty.cyberly.common.security.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.status(201).body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletRequest httpRequest
    ) {
        return ResponseEntity.ok(userService.login(request, httpRequest));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe() {
        Long userId = SecurityUtils.getAuthenticatedUserId();
        return ResponseEntity.ok(userService.getMe(userId));
    }
}