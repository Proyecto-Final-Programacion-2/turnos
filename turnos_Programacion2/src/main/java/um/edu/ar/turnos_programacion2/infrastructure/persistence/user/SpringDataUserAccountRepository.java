package um.edu.ar.turnos_programacion2.infrastructure.persistence.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataUserAccountRepository extends JpaRepository<UserAccountJpaEntity, Long> {
    boolean existsByLogin(String login);

    boolean existsByEmail(String email);

    Optional<UserAccountJpaEntity> findByLogin(String login);
}
