package um.edu.ar.turnos_programacion2.domain.user.port.in;

public record RegisterUserCommand(
    String login,
    String password,
    String firstName,
    String lastName,
    String email,
    String imageUrl,
    String langKey
) {
}
