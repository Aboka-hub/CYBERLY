package cb.empty.cyberly.risk.api;

import cb.empty.cyberly.accounts.domain.User;
import cb.empty.cyberly.accounts.infra.UserRepository;
import cb.empty.cyberly.common.security.SecurityUtils;
import cb.empty.cyberly.risk.domain.RiskSnapshot;
import cb.empty.cyberly.risk.infra.RiskSnapshotRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/risk")
@RequiredArgsConstructor
@Tag(name = "Risk", description = "Риск-анализ аккаунта")
@SecurityRequirement(name = "bearerAuth")
public class RiskController {

    private final RiskSnapshotRepository riskSnapshotRepository;
    private final UserRepository userRepository;

    @Operation(
        summary = "Последний риск-снапшот",
        description = "Возвращает актуальный расчёт риска. Пересчитывается автоматически при каждом входе. Только владелец."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Снапшот риска"),
        @ApiResponse(responseCode = "404", description = "Риск ещё не рассчитан"),
        @ApiResponse(responseCode = "403", description = "Доступ запрещён — чужие данные")
    })
    @GetMapping("/{userId}")
    public RiskSnapshot getRisk(@PathVariable Long userId) {
        SecurityUtils.requireOwner(userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return riskSnapshotRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Risk not calculated yet"));
    }
}
