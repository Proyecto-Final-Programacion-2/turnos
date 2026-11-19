package um.edu.ar.turnos_programacion2.domain.user.model;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

public record UserAccount(
    Long id,
    String login,
    String passwordHash,
    // ID estable enviado a la catedra; no es el ID interno ni lo elige el cliente.
    String externalPatientId,
    String firstName,
    String lastName,
    String email,
    boolean activated,
    String imageUrl,
    String langKey,
    Instant createdDate,
    Set<String> authorities
) {
    public UserAccount {
        authorities = Set.copyOf(authorities);
    }

    public static UserAccount registered(
        String login,
        String passwordHash,
        String firstName,
        String lastName,
        String email,
        String imageUrl,
        String langKey,
        Instant createdDate
    ) {
        // El identificador se genera en el backend y luego se conserva en la base.
        return new UserAccount(
            null,
            login,
            passwordHash,
            "user-" + UUID.randomUUID(),
            firstName,
            lastName,
            email,
            true,
            imageUrl,
            langKey,
            createdDate,
            Set.of("ROLE_USER")
        );
    }
}
