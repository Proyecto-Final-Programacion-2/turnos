package um.edu.ar.turnos_programacion2.infrastructure.persistence.reservation;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.edu.ar.turnos_programacion2.domain.reservation.exception.ReservationOwnerMismatchException;
import um.edu.ar.turnos_programacion2.domain.reservation.model.ReservationProcess;
import um.edu.ar.turnos_programacion2.domain.reservation.port.out.ReservationProcessRepository;
import um.edu.ar.turnos_programacion2.infrastructure.persistence.user.SpringDataUserAccountRepository;
import um.edu.ar.turnos_programacion2.infrastructure.persistence.user.UserAccountJpaEntity;

import java.util.Optional;

// Adapta el puerto del dominio a las tablas JPA propias de Turnos.
@Component
public class JpaReservationProcessRepositoryAdapter implements ReservationProcessRepository {

    private final SpringDataReservationProcessRepository repository;
    private final SpringDataUserAccountRepository userAccountRepository;

    public JpaReservationProcessRepositoryAdapter(
        SpringDataReservationProcessRepository repository,
        SpringDataUserAccountRepository userAccountRepository
    ) {
        this.repository = repository;
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    @Transactional
    public ReservationProcess save(ReservationProcess reservationProcess) {
        // Resuelve el usuario dentro de la transaccion y verifica que sus dos IDs correspondan.
        UserAccountJpaEntity owner = userAccountRepository.findById(reservationProcess.ownerUserId())
            .orElseThrow(() -> new IllegalArgumentException("ownerUserId does not exist"));
        if (!owner.getExternalPatientId().equals(reservationProcess.externalPatientId())) {
            throw new ReservationOwnerMismatchException();
        }
        return repository.save(new ReservationProcessJpaEntity(reservationProcess, owner)).toDomain();
    }

    @Override
    public Optional<ReservationProcess> findByProcessIdAndOwner(String reservationProcessId, Long ownerUserId) {
        // La consulta limita por proceso y dueño en la base, no filtra despues en memoria.
        return repository.findByReservationProcessIdAndOwner_Id(reservationProcessId, ownerUserId)
            .map(ReservationProcessJpaEntity::toDomain);
    }
}
