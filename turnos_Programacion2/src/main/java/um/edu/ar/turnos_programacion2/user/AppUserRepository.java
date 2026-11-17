package um.edu.ar.turnos_programacion2.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Spring Data implementa estas consultas a partir del nombre de cada metodo.
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    Optional<AppUser> findByLogin(String login);
}
