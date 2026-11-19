package um.edu.ar.turnos_programacion2.infrastructure.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import um.edu.ar.turnos_programacion2.domain.user.exception.InvalidCredentialsException;
import um.edu.ar.turnos_programacion2.domain.user.model.UserAccount;
import um.edu.ar.turnos_programacion2.domain.user.port.out.UserAuthenticator;

@Component
public class SpringSecurityUserAuthenticator implements UserAuthenticator {

    private final AuthenticationManager authenticationManager;

    public SpringSecurityUserAuthenticator(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserAccount authenticate(String username, String password) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(username, password)
            );
        } catch (AuthenticationException exception) {
            throw new InvalidCredentialsException();
        }

        if (!(authentication.getPrincipal() instanceof AppUserDetails userDetails)) {
            throw new IllegalStateException("Authentication returned an unexpected principal type");
        }
        return userDetails.getUserAccount();
    }
}
