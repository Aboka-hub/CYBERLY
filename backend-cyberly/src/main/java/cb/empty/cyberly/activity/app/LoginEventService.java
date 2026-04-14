package cb.empty.cyberly.activity.app;

import cb.empty.cyberly.accounts.domain.User;
import cb.empty.cyberly.accounts.infra.UserRepository;
import cb.empty.cyberly.activity.domain.LoginEvent;
import cb.empty.cyberly.activity.domain.enums.LoginEventType;
import cb.empty.cyberly.activity.infra.LoginEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoginEventService {

    private final LoginEventRepository loginEventRepository;
    private final UserRepository userRepository;

    public LoginEvent recordEvent(User user,
                                  LoginEventType type,
                                  String ip,
                                  String country,
                                  String device) {

        LoginEvent event = new LoginEvent();
        event.setUser(user);
        event.setType(type);
        event.setIpAddress(ip);
        event.setCountry(country);
        event.setDevice(device);

        return loginEventRepository.save(event);
    }

    public List<LoginEvent> getHistory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));
        return loginEventRepository.findTop20ByUserOrderByCreatedAtDesc(user);
    }
}
