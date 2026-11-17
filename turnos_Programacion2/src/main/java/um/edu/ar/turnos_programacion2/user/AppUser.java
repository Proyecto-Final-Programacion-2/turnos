package um.edu.ar.turnos_programacion2.user;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "jhi_user")
// Cuenta de usuario final de la app; no representa la cuenta tecnica de la catedra.
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String login;

    // BCrypt guarda un hash irreversible, nunca la contrasena original.
    @Column(name = "password_hash", nullable = false, length = 60)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false)
    private boolean activated;

    @Column(name = "image_url", length = 256)
    private String imageUrl;

    @Column(name = "lang_key", nullable = false, length = 10)
    private String langKey;

    @Column(name = "created_date", nullable = false)
    private Instant createdDate;

    // Las autoridades se guardan en una tabla propia vinculada por user_id.
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "jhi_user_authority",
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "authority_name", nullable = false, length = 50)
    private Set<String> authorities = new HashSet<>();

    protected AppUser() {
        // JPA necesita un constructor sin argumentos para reconstruir la entidad.
    }

    public AppUser(
        String login,
        String passwordHash,
        String firstName,
        String lastName,
        String email,
        String imageUrl,
        String langKey
    ) {
        this.login = login;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.imageUrl = imageUrl;
        this.langKey = langKey;
        // El alta activa inmediatamente la cuenta y asigna un rol basico controlado por el servidor.
        this.activated = true;
        this.createdDate = Instant.now();
        this.authorities.add("ROLE_USER");
    }

    public Long getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isActivated() {
        return activated;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLangKey() {
        return langKey;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public Set<String> getAuthorities() {
        return Set.copyOf(authorities);
    }
}
