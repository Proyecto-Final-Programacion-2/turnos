package um.edu.ar.turnos_programacion2.infrastructure.web.user;

import um.edu.ar.turnos_programacion2.domain.user.model.UserAccount;
import java.util.Set;

public record UserResponse(
    Long id,
    String login,
    String firstName,
    String lastName,
    String email,
    boolean activated,
    String imageUrl,
    String langKey,
    Set<String> authorities
) {
    public static UserResponse from(UserAccount user) {
        return new UserResponse(
            user.id(),
            user.login(),
            user.firstName(),
            user.lastName(),
            user.email(),
            user.activated(),
            user.imageUrl(),
            user.langKey(),
            user.authorities()
        );
    }
}
