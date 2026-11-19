package um.edu.ar.turnos_programacion2.domain.user.port.out;

import um.edu.ar.turnos_programacion2.domain.user.model.UserAccount;

public interface AccessTokenIssuer {
    String issueFor(UserAccount userAccount);
}
