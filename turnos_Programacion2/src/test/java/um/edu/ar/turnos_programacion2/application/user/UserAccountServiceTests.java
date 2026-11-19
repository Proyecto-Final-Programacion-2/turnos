package um.edu.ar.turnos_programacion2.application.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import um.edu.ar.turnos_programacion2.domain.user.exception.AccountConflictException;
import um.edu.ar.turnos_programacion2.domain.user.exception.InvalidCredentialsException;
import um.edu.ar.turnos_programacion2.domain.user.model.UserAccount;
import um.edu.ar.turnos_programacion2.domain.user.port.in.RegisterUserCommand;
import um.edu.ar.turnos_programacion2.domain.user.port.out.AccessTokenIssuer;
import um.edu.ar.turnos_programacion2.domain.user.port.out.PasswordHasher;
import um.edu.ar.turnos_programacion2.domain.user.port.out.UserAuthenticator;
import um.edu.ar.turnos_programacion2.domain.user.port.out.UserAccountRepository;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserAccountServiceTests {

    private UserAccountRepository userAccountRepository;
    private PasswordHasher passwordHasher;
    private UserAuthenticator userAuthenticator;
    private AccessTokenIssuer accessTokenIssuer;
    private UserAccountService service;

    @BeforeEach
    void setUp() {
        userAccountRepository = mock(UserAccountRepository.class);
        passwordHasher = mock(PasswordHasher.class);
        userAuthenticator = mock(UserAuthenticator.class);
        accessTokenIssuer = mock(AccessTokenIssuer.class);
        service = new UserAccountService(userAccountRepository, passwordHasher, userAuthenticator, accessTokenIssuer);
    }

    @Test
    void registrationNormalizesValuesAndDelegatesPersistenceThroughPorts() {
        RegisterUserCommand command = new RegisterUserCommand(
            " Alice ",
            "secret",
            " Alice ",
            " Example ",
            " ALICE@example.com ",
            null,
            null
        );
        when(passwordHasher.hash("secret")).thenReturn("bcrypt-hash");
        when(userAccountRepository.save(org.mockito.ArgumentMatchers.any(UserAccount.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        UserAccount result = service.register(command);

        assertThat(result.login()).isEqualTo("alice");
        assertThat(result.email()).isEqualTo("alice@example.com");
        assertThat(result.passwordHash()).isEqualTo("bcrypt-hash");
        assertThat(result.activated()).isTrue();
        assertThat(result.langKey()).isEqualTo("es");
        assertThat(result.authorities()).isEqualTo(Set.of("ROLE_USER"));
        verify(userAccountRepository).save(result);
    }

    @Test
    void registrationRejectsDuplicateLoginBeforeHashingOrSaving() {
        when(userAccountRepository.existsByLogin("alice")).thenReturn(true);

        assertThatThrownBy(() -> service.register(command("Alice", "alice@example.com")))
            .isInstanceOf(AccountConflictException.class)
            .hasMessage("LOGIN_ALREADY_EXISTS");

        verify(passwordHasher, org.mockito.Mockito.never()).hash(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void authenticationUsesGenericFailureForUnknownOrIncorrectCredentials() {
        when(userAuthenticator.authenticate("alice", "wrong")).thenThrow(new InvalidCredentialsException());

        assertThatThrownBy(() -> service.authenticate("Alice", "wrong"))
            .isInstanceOf(InvalidCredentialsException.class);
    }

    @Test
    void authenticationIssuesTokenOnlyForActiveAccountWithMatchingPassword() {
        UserAccount account = new UserAccount(
            7L,
            "alice",
            "bcrypt-hash",
            "Alice",
            "Example",
            "alice@example.com",
            true,
            null,
            "es",
            Instant.now(),
            Set.of("ROLE_USER")
        );
        when(userAuthenticator.authenticate("alice", "secret")).thenReturn(account);
        when(accessTokenIssuer.issueFor(account)).thenReturn("signed-token");

        assertThat(service.authenticate("Alice", "secret")).isEqualTo("signed-token");
        verify(userAuthenticator).authenticate("alice", "secret");
        verify(accessTokenIssuer).issueFor(account);
    }

    private RegisterUserCommand command(String login, String email) {
        return new RegisterUserCommand(login, "secret", "Alice", "Example", email, null, null);
    }
}
