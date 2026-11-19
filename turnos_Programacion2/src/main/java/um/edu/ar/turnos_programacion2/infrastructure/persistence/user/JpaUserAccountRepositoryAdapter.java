package um.edu.ar.turnos_programacion2.infrastructure.persistence.user;

import org.springframework.stereotype.Repository;
import um.edu.ar.turnos_programacion2.domain.user.model.UserAccount;
import um.edu.ar.turnos_programacion2.domain.user.port.out.UserAccountRepository;

import java.util.Optional;

@Repository
public class JpaUserAccountRepositoryAdapter implements UserAccountRepository {

    private final SpringDataUserAccountRepository repository;

    public JpaUserAccountRepositoryAdapter(SpringDataUserAccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByLogin(String login) {
        return repository.existsByLogin(login);
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public Optional<UserAccount> findByLogin(String login) {
        return repository.findByLogin(login).map(UserAccountJpaEntity::toDomain);
    }

    @Override
    public Optional<UserAccount> findById(Long id) {
        return repository.findById(id).map(UserAccountJpaEntity::toDomain);
    }

    @Override
    public UserAccount save(UserAccount userAccount) {
        return repository.save(new UserAccountJpaEntity(userAccount)).toDomain();
    }
}
