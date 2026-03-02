package cb.empty.cyberly.activity.api;

import cb.empty.cyberly.accounts.domain.User;
import cb.empty.cyberly.accounts.infra.UserRepository;
import cb.empty.cyberly.activity.domain.LoginEvent;
import cb.empty.cyberly.activity.infra.LoginEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping("/login-events")
public class LoginEventController {
    private final LoginEventRepository loginEventRepository;
    private final UserRepository userRepository;

    @GetMapping("/{userId}")
    public List<LoginEvent> getHistory(@PathVariable Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        return loginEventRepository
                .findTop20ByUserOrderByCreatedAtDesc(user);
    }
}
