package um.edu.ar.turnos_programacion2.domain.user.port.in;

import um.edu.ar.turnos_programacion2.domain.user.model.UserAccount;

public interface RegisterUserUseCase {
    UserAccount register(RegisterUserCommand command);
}
