package cb.empty.cyberly.accounts.api;

import cb.empty.cyberly.accounts.app.RegisterUserUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase userService;

    @PostMapping("/register")
    public String register(@RequestBody @Valid RegisterRequest request) {
        userService.register(request);
        return "User registered successfully";
    }
}