package cb.empty.cyberly.subscription.api;

import cb.empty.cyberly.common.security.SecurityUtils;
import cb.empty.cyberly.subscription.api.dto.SubscriptionRequest;
import cb.empty.cyberly.subscription.app.SubscriptionService;
import cb.empty.cyberly.subscription.domain.Subscription;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Управление платными подписками пользователя")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Operation(summary = "Добавить подписку", description = "Создаёт новую подписку. Только владелец.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Подписка добавлена"),
        @ApiResponse(responseCode = "400", description = "Невалидные данные"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @PostMapping("/{userId}")
    public ResponseEntity<String> addSubscription(
            @PathVariable Long userId,
            @RequestBody @Valid SubscriptionRequest request
    ) {
        SecurityUtils.requireOwner(userId);
        subscriptionService.addSubscription(userId, request);
        return ResponseEntity.status(201).body("Subscription added");
    }

    @Operation(summary = "Список подписок", description = "Возвращает все подписки пользователя (активные и истёкшие). Только владелец.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Список подписок"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    @GetMapping("/{userId}")
    public List<Subscription> getUserSubscriptions(@PathVariable Long userId) {
        SecurityUtils.requireOwner(userId);
        return subscriptionService.getUserSubscriptions(userId);
    }

    @Operation(summary = "Деактивировать подписку", description = "Устанавливает `active = false`. Только владелец подписки.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Подписка деактивирована"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён"),
        @ApiResponse(responseCode = "404", description = "Подписка не найдена")
    })
    @DeleteMapping("/{userId}/{subscriptionId}")
    public ResponseEntity<String> deactivate(
            @PathVariable Long userId,
            @PathVariable Long subscriptionId
    ) {
        SecurityUtils.requireOwner(userId);
        subscriptionService.deactivate(userId, subscriptionId);
        return ResponseEntity.ok("Subscription deactivated");
    }
}
