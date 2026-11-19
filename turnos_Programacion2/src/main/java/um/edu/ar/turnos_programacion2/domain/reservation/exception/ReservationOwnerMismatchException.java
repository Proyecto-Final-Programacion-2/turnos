package um.edu.ar.turnos_programacion2.domain.reservation.exception;

// Evita asociar a un proceso un externalPatientId que pertenece a otra cuenta local.
public class ReservationOwnerMismatchException extends RuntimeException {
    public ReservationOwnerMismatchException() {
        super("externalPatientId does not belong to ownerUserId");
    }
}
