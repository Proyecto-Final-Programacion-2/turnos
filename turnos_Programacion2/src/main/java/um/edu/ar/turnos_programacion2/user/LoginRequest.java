package um.edu.ar.turnos_programacion2.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Credenciales recibidas por el endpoint de inicio de sesion.
public record LoginRequest(
    @NotBlank
    @Size(max = 254)
    String username,

    @NotBlank
    @Size(min = 4, max = 100)
    String password
) {
}
