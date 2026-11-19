package um.edu.ar.turnos_programacion2.domain.user.port.out;

public interface PasswordHasher {
    String hash(String rawPassword);
}
