package um.edu.ar.turnos_programacion2.domain.reservation.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import um.edu.ar.turnos_programacion2.domain.user.model.UserAccount;

// Modelo del proceso local; conserva tanto la propiedad del usuario como los IDs de la catedra.
public record ReservationProcess(
    Long id,
    Long ownerUserId,
    String externalPatientId,
    String holdId,
    String reservationProcessId,
    Long reservationId,
    Long professionalId,
    LocalDate appointmentDate,
    LocalTime startTime,
    LocalTime endTime,
    ReservationStatus status,
    Instant expiresAt,
    Instant createdAt,
    Instant updatedAt,
    Instant confirmedAt,
    Instant cancelledAt
) {
    public ReservationProcess {
        // Las invariantes evitan persistir un turno incompleto o una ventana horaria imposible.
        if (ownerUserId == null || ownerUserId < 1) {
            throw new IllegalArgumentException("ownerUserId must be positive");
        }
        requireText(externalPatientId, "externalPatientId");
        if (externalPatientId.length() > 100) {
            throw new IllegalArgumentException("externalPatientId must not exceed 100 characters");
        }
        requireText(holdId, "holdId");
        requireText(reservationProcessId, "reservationProcessId");
        if (professionalId == null || professionalId < 1) {
            throw new IllegalArgumentException("professionalId must be positive");
        }
        Objects.requireNonNull(appointmentDate, "appointmentDate is required");
        Objects.requireNonNull(startTime, "startTime is required");
        Objects.requireNonNull(endTime, "endTime is required");
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("endTime must be after startTime");
        }
        Objects.requireNonNull(status, "status is required");
        Objects.requireNonNull(expiresAt, "expiresAt is required");
        Objects.requireNonNull(createdAt, "createdAt is required");
        Objects.requireNonNull(updatedAt, "updatedAt is required");
    }

    public static ReservationProcess held(
        UserAccount owner,
        String holdId,
        String reservationProcessId,
        Long professionalId,
        LocalDate appointmentDate,
        LocalTime startTime,
        LocalTime endTime,
        Instant expiresAt,
        Instant createdAt
    ) {
        // Un hold solo puede crearse para una cuenta persistida; la identidad externa sale de esa cuenta.
        Objects.requireNonNull(owner, "owner is required");
        if (owner.id() == null) {
            throw new IllegalArgumentException("owner must be persisted");
        }
        return new ReservationProcess(
            null,
            owner.id(),
            owner.externalPatientId(),
            holdId,
            reservationProcessId,
            null,
            professionalId,
            appointmentDate,
            startTime,
            endTime,
            ReservationStatus.HELD,
            expiresAt,
            createdAt,
            createdAt,
            null,
            null
        );
    }

    private static void requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
    }
}
