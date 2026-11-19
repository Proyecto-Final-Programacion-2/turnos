package um.edu.ar.turnos_programacion2.domain.user.port.out;

import um.edu.ar.turnos_programacion2.domain.user.model.UserAccount;

public interface UserAuthenticator {
    UserAccount authenticate(String username, String password);
}
