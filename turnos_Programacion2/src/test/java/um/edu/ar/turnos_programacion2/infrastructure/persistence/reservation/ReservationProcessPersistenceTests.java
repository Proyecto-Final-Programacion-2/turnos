package um.edu.ar.turnos_programacion2.infrastructure.persistence.reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import um.edu.ar.turnos_programacion2.domain.reservation.model.ReservationProcess;
import um.edu.ar.turnos_programacion2.domain.reservation.model.ReservationStatus;
import um.edu.ar.turnos_programacion2.domain.reservation.port.out.ReservationProcessRepository;
import um.edu.ar.turnos_programacion2.domain.reservation.exception.ReservationOwnerMismatchException;
import um.edu.ar.turnos_programacion2.domain.user.model.UserAccount;
import um.edu.ar.turnos_programacion2.domain.user.port.out.UserAccountRepository;
import um.edu.ar.turnos_programacion2.infrastructure.persistence.user.SpringDataUserAccountRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
// Comprueba migraciones, conversion JPA/dominio y aislamiento por propietario con H2.
class ReservationProcessPersistenceTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringDataUserAccountRepository userAccountRepository;

    @Autowired
    private UserAccountRepository userAccountPort;

    @Autowired
    private SpringDataReservationProcessRepository springDataRepository;

    @Autowired
    private ReservationProcessRepository reservationProcessRepository;

    @BeforeEach
    void cleanDatabase() {
        springDataRepository.deleteAll();
        userAccountRepository.deleteAll();
    }

    @Test
    // Un proceso solo se recupera con su usuario y no admite el ID externo de otra cuenta.
    void persistsReservationProcessWithItsOwnerAndExternalIdentifiers() throws Exception {
        register("alice", "alice@example.com");
        register("bob", "bob@example.com");

        var alice = userAccountRepository.findByLogin("alice").orElseThrow();
        var bob = userAccountRepository.findByLogin("bob").orElseThrow();
        UserAccount aliceAccount = userAccountPort.findByLogin("alice").orElseThrow();
        assertThat(alice.getExternalPatientId()).isNotEqualTo(bob.getExternalPatientId());
        Instant now = Instant.parse("2026-11-19T12:00:00Z");
        ReservationProcess process = ReservationProcess.held(
            aliceAccount,
            "hold-alice",
            "process-alice",
            15L,
            LocalDate.parse("2026-11-20"),
            LocalTime.parse("09:30"),
            LocalTime.parse("10:00"),
            now.plusSeconds(600),
            now
        );

        ReservationProcess saved = reservationProcessRepository.save(process);

        assertThat(saved.id()).isNotNull();
        assertThat(saved.ownerUserId()).isEqualTo(alice.getId());
        assertThat(saved.externalPatientId()).isEqualTo(alice.getExternalPatientId());
        assertThat(saved.status()).isEqualTo(ReservationStatus.HELD);
        assertThat(saved.reservationId()).isNull();
        assertThat(reservationProcessRepository.findByProcessIdAndOwner("process-alice", alice.getId()))
            .contains(saved);
        assertThat(reservationProcessRepository.findByProcessIdAndOwner("process-alice", bob.getId()))
            .isEmpty();

        ReservationProcess wrongPatient = new ReservationProcess(
            null,
            alice.getId(),
            bob.getExternalPatientId(),
            "hold-wrong-owner",
            "process-wrong-owner",
            null,
            15L,
            LocalDate.parse("2026-11-20"),
            LocalTime.parse("09:30"),
            LocalTime.parse("10:00"),
            ReservationStatus.HELD,
            now.plusSeconds(600),
            now,
            now,
            null,
            null
        );
        assertThatThrownBy(() -> reservationProcessRepository.save(wrongPatient))
            .isInstanceOf(ReservationOwnerMismatchException.class)
            .hasMessage("externalPatientId does not belong to ownerUserId");
    }

    private void register(String login, String email) throws Exception {
        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "login": "%s",
                      "password": "secure-password",
                      "firstName": "Test",
                      "lastName": "User",
                      "email": "%s"
                    }
                    """.formatted(login, email)))
            .andExpect(status().isCreated());
    }
}
