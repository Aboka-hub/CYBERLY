package cb.empty.cyberly.activity.domain;

import cb.empty.cyberly.accounts.domain.User;
import cb.empty.cyberly.activity.domain.enums.LoginEventType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_events")
@Getter
@Setter
public class LoginEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginEventType type;

    @Column(nullable = false)
    private String ipAddress;

    private String country;
    private String device;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}