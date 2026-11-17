package um.edu.ar.turnos_programacion2.user;

import java.util.Set;

// Respuesta publica que omite tanto la contrasena como su hash.
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
    // Convierte la entidad persistida en un objeto seguro para exponer por HTTP.
    public static UserResponse from(AppUser user) {
        return new UserResponse(
            user.getId(),
            user.getLogin(),
            user.getFirstName(),
            user.getLastName(),
            user.getEmail(),
            user.isActivated(),
            user.getImageUrl(),
            user.getLangKey(),
            user.getAuthorities()
        );
    }
}
