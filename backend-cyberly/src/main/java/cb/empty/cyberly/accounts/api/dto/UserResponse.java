package cb.empty.cyberly.accounts.api.dto;

import cb.empty.cyberly.accounts.domain.emuns.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {

    private String token;
    private Long userId;
    private String email;
    private Status status;
    private String message;
}