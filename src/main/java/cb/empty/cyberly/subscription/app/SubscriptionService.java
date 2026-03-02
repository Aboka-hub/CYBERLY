package cb.empty.cyberly.subscription.app;

import cb.empty.cyberly.accounts.domain.User;
import cb.empty.cyberly.accounts.infra.UserRepository;
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

    public void addSubscription(Long userId,
                                String serviceName,
                                LocalDate nextPaymentDate) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        Subscription subscription = new Subscription();
        subscription.setUser(user);
        subscription.setServiceName(serviceName);
        subscription.setNextPaymentDate(nextPaymentDate);
        subscription.setActive(true);

        subscriptionRepository.save(subscription);
    }

    public List<Subscription> getUserSubscriptions(Long userId) {
        return subscriptionRepository.findByUserId(userId);
    }

    @Scheduled(cron = "0 0 0 * * ?") // каждый день в 00:00
    public void checkExpiredSubscriptions() {

        LocalDate today = LocalDate.now();

        List<Subscription> activeSubscriptions =
                subscriptionRepository.findByNextPaymentDateAndActive(today, true);

        for (Subscription sub : activeSubscriptions) {
            sub.setActive(false);
        }

        subscriptionRepository.saveAll(activeSubscriptions);
    }
}