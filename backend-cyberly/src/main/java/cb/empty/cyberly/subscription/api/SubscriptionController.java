package cb.empty.cyberly.subscription.api;

import cb.empty.cyberly.common.security.SecurityUtils;
import cb.empty.cyberly.subscription.api.dto.SubscriptionRequest;
import cb.empty.cyberly.subscription.app.SubscriptionService;
import cb.empty.cyberly.subscription.domain.Subscription;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/{userId}")
    public ResponseEntity<String> addSubscription(
            @PathVariable Long userId,
            @RequestBody @Valid SubscriptionRequest request
    ) {
        SecurityUtils.requireOwner(userId);
        subscriptionService.addSubscription(userId, request);
        return ResponseEntity.status(201).body("Subscription added");
    }

    @GetMapping("/{userId}")
    public List<Subscription> getUserSubscriptions(@PathVariable Long userId) {
        SecurityUtils.requireOwner(userId);
        return subscriptionService.getUserSubscriptions(userId);
    }

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