package um.edu.ar.turnos_programacion2.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// DTO de entrada: la validacion ocurre antes de llamar a la logica de registro.
public record RegisterRequest(
    @NotBlank
    @Size(max = 50)
    // Caracteres permitidos por el formato de login compatible con JHipster.
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

    // Campo opcional; si se omite, el servicio asigna "es".
    @Pattern(regexp = "^[A-Za-z0-9_-]{2,10}$")
    String langKey
) {
}
