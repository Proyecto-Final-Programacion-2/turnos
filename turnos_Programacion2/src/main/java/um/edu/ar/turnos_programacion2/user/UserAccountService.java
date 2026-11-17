package um.edu.ar.turnos_programacion2.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class UserAccountService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountService(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AppUser register(RegisterRequest request) {
        // Normalizar antes de revisar duplicados evita cuentas con distinta capitalizacion.
        String login = request.login().trim().toLowerCase(Locale.ROOT);
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByLogin(login)) {
            throw new AccountConflictException("LOGIN_ALREADY_EXISTS", "login");
        }
        if (userRepository.existsByEmail(email)) {
            throw new AccountConflictException("EMAIL_ALREADY_EXISTS", "email");
        }

        // La contrasena se transforma antes de guardar; el texto recibido no se persiste.
        String langKey = request.langKey() == null ? "es" : request.langKey().trim();
        AppUser user = new AppUser(
            login,
            passwordEncoder.encode(request.password()),
            request.firstName().trim(),
            request.lastName().trim(),
            email,
            request.imageUrl() == null ? null : request.imageUrl().trim(),
            langKey
        );
        return userRepository.save(user);
    }
}
