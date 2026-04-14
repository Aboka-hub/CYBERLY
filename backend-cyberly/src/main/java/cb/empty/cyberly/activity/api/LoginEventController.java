package cb.empty.cyberly.activity.api;

import cb.empty.cyberly.activity.app.LoginEventService;
import cb.empty.cyberly.activity.domain.LoginEvent;
import cb.empty.cyberly.common.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login-events")
public class LoginEventController {

    private final LoginEventService loginEventService;

    @GetMapping("/{userId}")
    public List<LoginEvent> getHistory(@PathVariable Long userId) {
        SecurityUtils.requireOwner(userId);
        return loginEventService.getHistory(userId);
    }
}
