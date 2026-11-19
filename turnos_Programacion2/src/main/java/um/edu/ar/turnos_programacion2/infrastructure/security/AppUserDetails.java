package um.edu.ar.turnos_programacion2.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import um.edu.ar.turnos_programacion2.domain.user.model.UserAccount;

import java.util.Collection;

public class AppUserDetails implements UserDetails {

    private final UserAccount userAccount;

    public AppUserDetails(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userAccount.authorities().stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getPassword() {
        return userAccount.passwordHash();
    }

    @Override
    public String getUsername() {
        return userAccount.login();
    }

    @Override
    public boolean isEnabled() {
        return userAccount.activated();
    }
}
