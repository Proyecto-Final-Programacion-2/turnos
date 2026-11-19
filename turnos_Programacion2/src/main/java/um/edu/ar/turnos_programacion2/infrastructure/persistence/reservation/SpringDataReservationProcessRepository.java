package um.edu.ar.turnos_programacion2.infrastructure.persistence.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// Spring Data implementa la busqueda incluyendo el dueño como parte de la condicion.
public interface SpringDataReservationProcessRepository extends JpaRepository<ReservationProcessJpaEntity, Long> {
    Optional<ReservationProcessJpaEntity> findByReservationProcessIdAndOwner_Id(
        String reservationProcessId,
        Long ownerId
    );
}
