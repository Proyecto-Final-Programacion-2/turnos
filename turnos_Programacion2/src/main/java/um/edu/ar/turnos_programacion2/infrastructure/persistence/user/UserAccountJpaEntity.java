package um.edu.ar.turnos_programacion2.infrastructure.persistence.user;

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
import um.edu.ar.turnos_programacion2.domain.user.model.UserAccount;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "jhi_user")
public class UserAccountJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String login;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "jhi_user_authority",
        joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "authority_name", nullable = false, length = 50)
    private Set<String> authorities = new HashSet<>();

    protected UserAccountJpaEntity() {
    }

    UserAccountJpaEntity(UserAccount account) {
        this.id = account.id();
        this.login = account.login();
        this.passwordHash = account.passwordHash();
        this.firstName = account.firstName();
        this.lastName = account.lastName();
        this.email = account.email();
        this.activated = account.activated();
        this.imageUrl = account.imageUrl();
        this.langKey = account.langKey();
        this.createdDate = account.createdDate();
        this.authorities = new HashSet<>(account.authorities());
    }

    UserAccount toDomain() {
        return new UserAccount(
            id,
            login,
            passwordHash,
            firstName,
            lastName,
            email,
            activated,
            imageUrl,
            langKey,
            createdDate,
            authorities
        );
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
}
