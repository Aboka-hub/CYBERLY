package cb.empty.cyberly.subscription.app;

import cb.empty.cyberly.accounts.domain.User;
import cb.empty.cyberly.accounts.infra.UserRepository;
import cb.empty.cyberly.subscription.api.dto.SubscriptionRequest;
import cb.empty.cyberly.subscription.domain.Subscription;
import cb.empty.cyberly.subscription.infra.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public void addSubscription(Long userId, SubscriptionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setServiceName(request.getServiceName());
        subscription.setAmount(request.getAmount());
        subscription.setNextPaymentDate(request.getNextPaymentDate());
        subscription.setActive(true);

        subscriptionRepository.save(subscription);
    }

    public List<Subscription> getUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    public void deactivate(Long userId, Long subscriptionId) {
        Subscription sub = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Subscription not found"
                ));

        if (!sub.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }

        sub.setActive(false);
        subscriptionRepository.save(sub);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void checkExpiredSubscriptions() {
        LocalDate today = LocalDate.now();
        List<Subscription> expired =
                subscriptionRepository.findByNextPaymentDateBeforeAndActive(today, true);
        expired.forEach(sub -> sub.setActive(false));
        subscriptionRepository.saveAll(expired);
    }
}