package um.edu.ar.turnos_programacion2.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;
import um.edu.ar.turnos_programacion2.user.AppUser;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
// Crea los JWT de usuario final que despues validan Turnos y Catalogo.
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final long expirationSeconds;

    public JwtTokenService(
        JwtEncoder jwtEncoder,
        @Value("${app.security.jwt.issuer}") String issuer,
        @Value("${app.security.jwt.expiration-seconds:3600}") long expirationSeconds
    ) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        if (expirationSeconds < 1) {
            throw new IllegalStateException("JWT_EXPIRATION_SECONDS must be positive");
        }
        this.expirationSeconds = expirationSeconds;
    }

    public String createToken(AppUser user) {
        Instant issuedAt = Instant.now();
        // subject identifica el login; user_id y authorities se usan para autorizar y asociar recursos.
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer(issuer)
            .issuedAt(issuedAt)
            .expiresAt(issuedAt.plus(expirationSeconds, ChronoUnit.SECONDS))
            .subject(user.getLogin())
            .claim("user_id", user.getId().toString())
            .claim("authorities", List.copyOf(user.getAuthorities()))
            .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        // Firma HS256 con la clave secreta externa compartida por los dos backends.
        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
