package um.edu.ar.turnos_programacion2.application.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.edu.ar.turnos_programacion2.domain.user.exception.AccountConflictException;
import um.edu.ar.turnos_programacion2.domain.user.exception.UserAccountNotFoundException;
import um.edu.ar.turnos_programacion2.domain.user.model.UserAccount;
import um.edu.ar.turnos_programacion2.domain.user.port.in.AuthenticateUserUseCase;
import um.edu.ar.turnos_programacion2.domain.user.port.in.GetUserAccountUseCase;
import um.edu.ar.turnos_programacion2.domain.user.port.in.RegisterUserCommand;
import um.edu.ar.turnos_programacion2.domain.user.port.in.RegisterUserUseCase;
import um.edu.ar.turnos_programacion2.domain.user.port.out.AccessTokenIssuer;
import um.edu.ar.turnos_programacion2.domain.user.port.out.PasswordHasher;
import um.edu.ar.turnos_programacion2.domain.user.port.out.UserAuthenticator;
import um.edu.ar.turnos_programacion2.domain.user.port.out.UserAccountRepository;

import java.time.Instant;
import java.util.Locale;

@Service
public class UserAccountService implements RegisterUserUseCase, AuthenticateUserUseCase, GetUserAccountUseCase {

    private final UserAccountRepository userAccountRepository;
    private final PasswordHasher passwordHasher;
    private final UserAuthenticator userAuthenticator;
    private final AccessTokenIssuer accessTokenIssuer;

    public UserAccountService(
        UserAccountRepository userAccountRepository,
        PasswordHasher passwordHasher,
        UserAuthenticator userAuthenticator,
        AccessTokenIssuer accessTokenIssuer
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordHasher = passwordHasher;
        this.userAuthenticator = userAuthenticator;
        this.accessTokenIssuer = accessTokenIssuer;
    }

    @Override
    @Transactional
    public UserAccount register(RegisterUserCommand command) {
        String login = command.login().trim().toLowerCase(Locale.ROOT);
        String email = command.email().trim().toLowerCase(Locale.ROOT);

        if (userAccountRepository.existsByLogin(login)) {
            throw new AccountConflictException("LOGIN_ALREADY_EXISTS", "login");
        }
        if (userAccountRepository.existsByEmail(email)) {
            throw new AccountConflictException("EMAIL_ALREADY_EXISTS", "email");
        }

        String langKey = command.langKey() == null ? "es" : command.langKey().trim();
        UserAccount account = UserAccount.registered(
            login,
            passwordHasher.hash(command.password()),
            command.firstName().trim(),
            command.lastName().trim(),
            email,
            command.imageUrl() == null ? null : command.imageUrl().trim(),
            langKey,
            Instant.now()
        );
        return userAccountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public String authenticate(String username, String password) {
        String normalizedLogin = username.trim().toLowerCase(Locale.ROOT);
        UserAccount userAccount = userAuthenticator.authenticate(normalizedLogin, password);
        return accessTokenIssuer.issueFor(userAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public UserAccount getById(Long id) {
        return userAccountRepository.findById(id).orElseThrow(UserAccountNotFoundException::new);
    }
}
