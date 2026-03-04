package cb.empty.cyberly.activity.app;

import cb.empty.cyberly.accounts.domain.User;
import cb.empty.cyberly.activity.domain.LoginEvent;
import cb.empty.cyberly.activity.domain.enums.LoginEventType;
import cb.empty.cyberly.activity.infra.LoginEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginEventService {

    private final LoginEventRepository loginEventRepository;

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
}
