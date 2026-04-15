package cb.empty.cyberly.subscription.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SubscriptionRequest {

    @NotBlank
    private String serviceName;

    @NotNull
    @DecimalMin("0.01")
    private Double amount;

    @NotNull
    private LocalDate nextPaymentDate;
}
