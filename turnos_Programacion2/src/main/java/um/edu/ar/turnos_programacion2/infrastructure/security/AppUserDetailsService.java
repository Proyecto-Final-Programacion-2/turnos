package um.edu.ar.turnos_programacion2.infrastructure.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import um.edu.ar.turnos_programacion2.domain.user.port.out.UserAccountRepository;

import java.util.Locale;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public AppUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String normalizedLogin = username.trim().toLowerCase(Locale.ROOT);
        return userAccountRepository.findByLogin(normalizedLogin)
            .map(AppUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
