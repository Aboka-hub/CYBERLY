package cb.empty.cyberly.risk.app;

import cb.empty.cyberly.accounts.domain.User;
import cb.empty.cyberly.activity.domain.LoginEvent;
import cb.empty.cyberly.activity.domain.LoginEventType;
import cb.empty.cyberly.risk.domain.RiskLevel;
import cb.empty.cyberly.accounts.domain.Status;
import cb.empty.cyberly.activity.infra.LoginEventRepository;
import cb.empty.cyberly.risk.infra.RiskSnapshotRepository;
import cb.empty.cyberly.accounts.infra.UserRepository;
import cb.empty.cyberly.risk.domain.RiskSnapshot;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RiskService {

    private final LoginEventRepository loginEventRepository;
    private final RiskSnapshotRepository riskSnapshotRepository;
    private final UserRepository userRepository;

    public void calculateRisk(User user, LoginEvent newEvent) {
        int score = 0;
        StringBuilder reasons = new StringBuilder();

        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        long attemptsLast10Min =
                loginEventRepository.countByUserAndCreatedAtAfter(user, tenMinutesAgo);

        if (attemptsLast10Min >= 8) {
            score += 30;
            reasons.append("Too many login attempts; ");
        }

        // 2️⃣ Many failed attempts
        long failedAttempts =
                loginEventRepository.countByUserAndTypeAndCreatedAtAfter(
                        user,
                        LoginEventType.LOGIN_FAILED,
                        tenMinutesAgo
                );

        if (failedAttempts >= 5) {
            score += 35;
            reasons.append("Multiple failed logins; ");
        }

        // 3️⃣ New IP
        boolean knownIp =
                loginEventRepository.existsByUserAndIpAddressAndCreatedAtAfter(
                        user,
                        newEvent.getIpAddress(),
                        thirtyDaysAgo
                );

        if (!knownIp) {
            score += 15;
            reasons.append("New IP address; ");
        }

        score = Math.min(score, 100);

        RiskLevel level = determineLevel(score);

        saveSnapshot(user, score, level, reasons.toString());

        // 4️⃣ Auto-block
        if (level == RiskLevel.CRITICAL) {
            user.setStatus(Status.BLOCKED);
            userRepository.save(user);
        }
    }

    private RiskLevel determineLevel(int score) {
        if (score >= 80) return RiskLevel.CRITICAL;
        if (score >= 60) return RiskLevel.HIGH;
        if (score >= 30) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

    private void saveSnapshot(User user, int score, RiskLevel level, String reasons) {

        RiskSnapshot snapshot =
                riskSnapshotRepository.findByUser(user)
                        .orElse(new RiskSnapshot());

        snapshot.setUser(user);
        snapshot.setScore(score);
        snapshot.setLevel(level);
        snapshot.setReasons(reasons);
        snapshot.setCalculatedAt(LocalDateTime.now());

        riskSnapshotRepository.save(snapshot);
    }
}
