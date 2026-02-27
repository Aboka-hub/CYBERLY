package cb.empty.cyberly.risk.infra;

import cb.empty.cyberly.risk.domain.RiskSnapshot;
import cb.empty.cyberly.accounts.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RiskSnapshotRepository extends JpaRepository<RiskSnapshot, Long> {

    Optional<RiskSnapshot> findByUser(User user);
}
