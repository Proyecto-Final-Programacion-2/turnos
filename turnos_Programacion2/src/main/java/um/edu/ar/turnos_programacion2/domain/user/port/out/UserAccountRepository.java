package um.edu.ar.turnos_programacion2.domain.user.port.out;

import um.edu.ar.turnos_programacion2.domain.user.model.UserAccount;

import java.util.Optional;

public interface UserAccountRepository {
    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    Optional<UserAccount> findByLogin(String login);

    Optional<UserAccount> findById(Long id);

    UserAccount save(UserAccount userAccount);
}
