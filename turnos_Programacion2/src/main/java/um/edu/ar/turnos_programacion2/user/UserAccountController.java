package um.edu.ar.turnos_programacion2.user;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.edu.ar.turnos_programacion2.security.AppUserDetails;
import um.edu.ar.turnos_programacion2.security.JwtTokenService;

@RestController
@RequestMapping("/api")
// Adapta las peticiones HTTP a los servicios de cuenta y autenticacion.
public class UserAccountController {

    private final UserAccountService accountService;
    private final AppUserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public UserAccountController(
        UserAccountService accountService,
        AppUserRepository userRepository,
        AuthenticationManager authenticationManager,
        JwtTokenService jwtTokenService
    ) {
        this.accountService = accountService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    // Endpoint publico: crea una cuenta activa y devuelve solo datos no sensibles.
    @PostMapping("/register")
    @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return UserResponse.from(accountService.register(request));
    }

    // Endpoint publico: delega la comparacion segura de la contrasena a Spring Security.
    @PostMapping("/authenticate")
    public TokenResponse authenticate(@Valid @RequestBody LoginRequest request) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                    request.username(),
                    request.password()
                )
            );
        } catch (BadCredentialsException exception) {
            // Respuesta uniforme: no revela si fallo el usuario o la contrasena.
            throw new InvalidCredentialsException();
        }

        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        return new TokenResponse(jwtTokenService.createToken(userDetails.getUser()));
    }

    // Endpoint protegido: el ID se toma del token validado, no de un parametro del cliente.
    @GetMapping("/account")
    public UserResponse account(@AuthenticationPrincipal Jwt jwt) {
        Long userId;
        try {
            userId = Long.valueOf(jwt.getClaimAsString("user_id"));
        } catch (NumberFormatException | NullPointerException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid access token");
        }

        AppUser user = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid access token"));
        return UserResponse.from(user);
    }
}
