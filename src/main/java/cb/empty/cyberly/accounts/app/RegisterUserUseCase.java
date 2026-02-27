package cb.empty.cyberly.accounts.app;

import cb.empty.cyberly.accounts.api.RegisterRequest;
import cb.empty.cyberly.accounts.domain.User;
import cb.empty.cyberly.accounts.domain.Status;
import cb.empty.cyberly.accounts.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(Status.ACTIVE);

        userRepository.save(user);
    }
}
