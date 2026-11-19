package um.edu.ar.turnos_programacion2.domain.reservation.port.out;

import um.edu.ar.turnos_programacion2.domain.reservation.model.ReservationProcess;

import java.util.Optional;

// Puerto de salida: la aplicacion pide guardar o buscar procesos sin conocer JPA.
public interface ReservationProcessRepository {
    ReservationProcess save(ReservationProcess reservationProcess);

    Optional<ReservationProcess> findByProcessIdAndOwner(
        String reservationProcessId,
        Long ownerUserId
    );
}
