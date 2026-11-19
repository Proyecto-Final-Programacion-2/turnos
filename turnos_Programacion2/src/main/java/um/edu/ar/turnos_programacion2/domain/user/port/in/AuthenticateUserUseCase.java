package um.edu.ar.turnos_programacion2.domain.user.port.in;

public interface AuthenticateUserUseCase {
    String authenticate(String username, String password);
}
