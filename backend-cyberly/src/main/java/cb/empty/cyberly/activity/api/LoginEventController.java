package cb.empty.cyberly.activity.api;

import cb.empty.cyberly.activity.api.dto.LoginEventResponse;
import cb.empty.cyberly.activity.app.LoginEventService;
import cb.empty.cyberly.common.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/login-events")
@Tag(name = "Login Events", description = "История входов пользователя")
@SecurityRequirement(name = "bearerAuth")
public class LoginEventController {

    private final LoginEventService loginEventService;

    @Operation(summary = "История входов", description = "Возвращает все события входа для пользователя. Только владелец.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список событий"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён — чужие данные")
    })
    @GetMapping("/{userId}")
    public List<LoginEventResponse> getHistory(@PathVariable Long userId) {
        SecurityUtils.requireOwner(userId);
        return loginEventService.getHistory(userId);
    }
}
