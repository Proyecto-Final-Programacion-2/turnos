package um.edu.ar.turnos_programacion2.infrastructure.web.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank
    @Size(max = 254)
    String username,

    @NotBlank
    @Size(min = 4, max = 100)
    String password
) {
}
