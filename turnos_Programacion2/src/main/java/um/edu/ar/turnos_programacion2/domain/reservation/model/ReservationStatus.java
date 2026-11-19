package um.edu.ar.turnos_programacion2.domain.reservation.model;

public enum ReservationStatus {
    // Estados previstos para reflejar el ciclo externo; PHONE_REJECTED permite volver a enviar el telefono.
    HELD,
    WAITING_FOR_PHONE,
    PHONE_REJECTED,
    CONFIRMED,
    CANCELLED,
    EXPIRED,
    INVALID
}
