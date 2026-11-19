package um.edu.ar.turnos_programacion2.infrastructure.web.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.edu.ar.turnos_programacion2.domain.user.model.UserAccount;
import um.edu.ar.turnos_programacion2.domain.user.port.in.AuthenticateUserUseCase;
import um.edu.ar.turnos_programacion2.domain.user.port.in.GetUserAccountUseCase;
import um.edu.ar.turnos_programacion2.domain.user.port.in.RegisterUserCommand;
import um.edu.ar.turnos_programacion2.domain.user.port.in.RegisterUserUseCase;

@RestController
@RequestMapping("/api")
public class UserAccountController {

    private final RegisterUserUseCase registerUser;
    private final AuthenticateUserUseCase authenticateUser;
    private final GetUserAccountUseCase getUserAccount;

    public UserAccountController(
        RegisterUserUseCase registerUser,
        AuthenticateUserUseCase authenticateUser,
        GetUserAccountUseCase getUserAccount
    ) {
        this.registerUser = registerUser;
        this.authenticateUser = authenticateUser;
        this.getUserAccount = getUserAccount;
    }

    @PostMapping("/register")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
            request.login(),
            request.password(),
            request.firstName(),
            request.lastName(),
            request.email(),
            request.imageUrl(),
            request.langKey()
        );
        return UserResponse.from(registerUser.register(command));
    }

    @PostMapping("/authenticate")
    public TokenResponse authenticate(@Valid @RequestBody LoginRequest request) {
        return new TokenResponse(authenticateUser.authenticate(request.username(), request.password()));
    }

    @GetMapping("/account")
    public UserResponse account(@AuthenticationPrincipal Jwt jwt) {
        Long userId;
        try {
            userId = Long.valueOf(jwt.getClaimAsString("user_id"));
        } catch (NumberFormatException | NullPointerException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid access token");
        }
        UserAccount user = getUserAccount.getById(userId);
        return UserResponse.from(user);
    }
}
