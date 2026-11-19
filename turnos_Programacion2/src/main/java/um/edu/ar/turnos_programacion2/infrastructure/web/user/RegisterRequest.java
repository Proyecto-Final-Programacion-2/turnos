package um.edu.ar.turnos_programacion2.infrastructure.web.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank
    @Size(max = 50)
    @Pattern(regexp = "^[_.@A-Za-z0-9-]+$")
    String login,

    @NotBlank
    @Size(min = 4, max = 100)
    String password,

    @NotBlank
    @Size(max = 50)
    String firstName,

    @NotBlank
    @Size(max = 50)
    String lastName,

    @NotBlank
    @Email
    @Size(min = 5, max = 254)
    String email,

    @Size(max = 256)
    String imageUrl,

    @Pattern(regexp = "^[A-Za-z0-9_-]{2,10}$")
    String langKey
) {
}
