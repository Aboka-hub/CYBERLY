package cb.empty.cyberly.activity.api.dto;

import cb.empty.cyberly.activity.domain.LoginEvent;
import cb.empty.cyberly.activity.domain.enums.LoginEventType;

import java.time.LocalDateTime;

public record LoginEventResponse(
        Long id,
        String userEmail,
        LoginEventType type,
        String ipAddress,
        String country,
        String device,
        LocalDateTime createdAt
) {
    public static LoginEventResponse from(LoginEvent e) {
        return new LoginEventResponse(
                e.getId(),
                e.getUser().getEmail(),
                e.getType(),
                e.getIpAddress(),
                e.getCountry(),
                e.getDevice(),
                e.getCreatedAt()
        );
    }
}
