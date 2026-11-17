package um.edu.ar.turnos_programacion2.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import um.edu.ar.turnos_programacion2.user.AppUserRepository;

import java.util.Locale;

@Service
// Busca las cuentas guardadas para que Spring pueda validar el inicio de sesion.
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository userRepository;

    public AppUserDetailsService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Usa la misma normalizacion aplicada durante el registro.
        String normalizedLogin = username.trim().toLowerCase(Locale.ROOT);
        return userRepository.findByLogin(normalizedLogin)
            .map(AppUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
