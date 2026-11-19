package um.edu.ar.turnos_programacion2.domain.user.model;

import java.time.Instant;
import java.util.Set;

public record UserAccount(
    Long id,
    String login,
    String passwordHash,
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
        return new UserAccount(
            null,
            login,
            passwordHash,
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
