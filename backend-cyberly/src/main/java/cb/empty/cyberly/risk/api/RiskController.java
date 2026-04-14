package cb.empty.cyberly.risk.api;

import cb.empty.cyberly.accounts.domain.User;
import cb.empty.cyberly.accounts.infra.UserRepository;
import cb.empty.cyberly.common.security.SecurityUtils;
import cb.empty.cyberly.risk.domain.RiskSnapshot;
import cb.empty.cyberly.risk.infra.RiskSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/risk")
@RequiredArgsConstructor
public class RiskController {

    private final RiskSnapshotRepository riskSnapshotRepository;
    private final UserRepository userRepository;

    @GetMapping("/{userId}")
    public RiskSnapshot getRisk(@PathVariable Long userId) {

        SecurityUtils.requireOwner(userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        return riskSnapshotRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Risk not calculated yet"
                ));
    }
}