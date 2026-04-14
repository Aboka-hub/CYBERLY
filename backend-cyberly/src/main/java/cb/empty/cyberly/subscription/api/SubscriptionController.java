package cb.empty.cyberly.subscription.api;

import cb.empty.cyberly.common.config.SecurityUtils;
import cb.empty.cyberly.subscription.app.SubscriptionService;
import cb.empty.cyberly.subscription.domain.Subscription;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping("/{userId}")
    public ResponseEntity<String> addSubscription(
            @PathVariable Long userId,
            @RequestParam String serviceName,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate nextPaymentDate
    ) {
        SecurityUtils.requireOwner(userId);
        subscriptionService.addSubscription(userId, serviceName, nextPaymentDate);
        return ResponseEntity.ok("Subscription added");
    }

    @GetMapping("/{userId}")
    public List<Subscription> getUserSubscriptions(@PathVariable Long userId) {
        SecurityUtils.requireOwner(userId);
        return subscriptionService.getUserSubscriptions(userId);
    }
}