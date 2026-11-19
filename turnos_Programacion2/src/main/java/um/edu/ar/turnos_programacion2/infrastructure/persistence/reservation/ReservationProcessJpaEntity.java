package um.edu.ar.turnos_programacion2.infrastructure.persistence.reservation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import um.edu.ar.turnos_programacion2.domain.reservation.model.ReservationProcess;
import um.edu.ar.turnos_programacion2.domain.reservation.model.ReservationStatus;
import um.edu.ar.turnos_programacion2.infrastructure.persistence.user.UserAccountJpaEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

// Representacion ORM separada del modelo de dominio para mantener JPA en infraestructura.
@Entity
@Table(
    name = "reservation_process",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_reservation_process_hold_id", columnNames = "hold_id"),
        @UniqueConstraint(name = "uk_reservation_process_external_id", columnNames = "reservation_process_id"),
        @UniqueConstraint(name = "uk_reservation_process_reservation_id", columnNames = "reservation_id")
    }
)
public class ReservationProcessJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private UserAccountJpaEntity owner;

    @Column(name = "external_patient_id", nullable = false, length = 100)
    private String externalPatientId;

    @Column(name = "hold_id", nullable = false, length = 255)
    private String holdId;

    @Column(name = "reservation_process_id", nullable = false, length = 255)
    private String reservationProcessId;

    @Column(name = "reservation_id")
    private Long reservationId;

    @Column(name = "professional_id", nullable = false)
    private Long professionalId;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ReservationStatus status;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "confirmed_at")
    private Instant confirmedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    protected ReservationProcessJpaEntity() {
    }

    ReservationProcessJpaEntity(ReservationProcess process, UserAccountJpaEntity owner) {
        this.id = process.id();
        this.owner = owner;
        this.externalPatientId = process.externalPatientId();
        this.holdId = process.holdId();
        this.reservationProcessId = process.reservationProcessId();
        this.reservationId = process.reservationId();
        this.professionalId = process.professionalId();
        this.appointmentDate = process.appointmentDate();
        this.startTime = process.startTime();
        this.endTime = process.endTime();
        this.status = process.status();
        this.expiresAt = process.expiresAt();
        this.createdAt = process.createdAt();
        this.updatedAt = process.updatedAt();
        this.confirmedAt = process.confirmedAt();
        this.cancelledAt = process.cancelledAt();
    }

    ReservationProcess toDomain() {
        // Convierte el FK relacional al ID simple que usa el dominio.
        return new ReservationProcess(
            id,
            owner.getId(),
            externalPatientId,
            holdId,
            reservationProcessId,
            reservationId,
            professionalId,
            appointmentDate,
            startTime,
            endTime,
            status,
            expiresAt,
            createdAt,
            updatedAt,
            confirmedAt,
            cancelledAt
        );
    }
}
