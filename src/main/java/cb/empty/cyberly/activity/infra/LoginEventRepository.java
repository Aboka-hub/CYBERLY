package cb.empty.cyberly.activity.infra;

import cb.empty.cyberly.activity.domain.LoginEvent;
import cb.empty.cyberly.accounts.domain.User;
import cb.empty.cyberly.activity.domain.LoginEventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginEventRepository extends JpaRepository<LoginEvent, Long> {

    List<LoginEvent> findTop20ByUserOrderByCreatedAtDesc(User user);

    long countByUserIdAndType(Long userId, LoginEventType type);

    long countByUserAndCreatedAtAfter(User user, LocalDateTime time);

    long countByUserAndTypeAndCreatedAtAfter(User user,
                                             LoginEventType type,
                                             LocalDateTime time);

    boolean existsByUserAndIpAddressAndCreatedAtAfter(User user,
                                                      String ip,
                                                      LocalDateTime time);
}
