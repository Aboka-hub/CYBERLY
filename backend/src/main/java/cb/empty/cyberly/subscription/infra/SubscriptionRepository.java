package cb.empty.cyberly.subscription.infra;

import cb.empty.cyberly.subscription.domain.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findByUserId(Long userId);

    List<Subscription> findByNextPaymentDateAndActive(LocalDate date, boolean active);
}