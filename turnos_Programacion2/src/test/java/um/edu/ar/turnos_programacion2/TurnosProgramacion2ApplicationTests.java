package um.edu.ar.turnos_programacion2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import um.edu.ar.turnos_programacion2.infrastructure.persistence.reservation.SpringDataReservationProcessRepository;
import um.edu.ar.turnos_programacion2.infrastructure.persistence.user.SpringDataUserAccountRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
// Pruebas de API con Spring real y una base efimera de test.
class TurnosProgramacion2ApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SpringDataUserAccountRepository userRepository;

    @Autowired
    private SpringDataReservationProcessRepository reservationProcessRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanUsers() {
        reservationProcessRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    // Verifica alta activa, valores por defecto, respuesta segura y hash BCrypt persistido.
    void registrationActivatesUserAndStoresOnlyPasswordHash() throws Exception {
        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "login": "Alice",
                      "password": "secure-password",
                      "firstName": "Alice",
                      "lastName": "Example",
                      "email": "Alice@example.com"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.login").value("alice"))
            .andExpect(jsonPath("$.email").value("alice@example.com"))
            .andExpect(jsonPath("$.activated").value(true))
            .andExpect(jsonPath("$.langKey").value("es"))
            .andExpect(jsonPath("$.authorities[0]").value("ROLE_USER"))
            .andExpect(jsonPath("$.password").doesNotExist())
            .andExpect(jsonPath("$.passwordHash").doesNotExist());

        var savedUser = userRepository.findByLogin("alice").orElseThrow();
        assertThat(savedUser.getPasswordHash()).isNotEqualTo("secure-password");
        assertThat(passwordEncoder.matches("secure-password", savedUser.getPasswordHash())).isTrue();
        assertThat(savedUser.getExternalPatientId()).startsWith("user-");
    }

    @Test
    // Login y email se comparan sin distinguir mayusculas/minusculas.
    void registrationRejectsDuplicateLoginAndEmail() throws Exception {
        register("alice", "alice@example.com");

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registrationJson("ALICE", "another@example.com")))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("LOGIN_ALREADY_EXISTS"));

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registrationJson("bob", "ALICE@example.com")))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"));
    }

    @Test
    // La validacion del DTO debe cortar el flujo antes de escribir en la base.
    void registrationRejectsInvalidFieldsWithoutCreatingAccount() throws Exception {
        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "login": "invalid login",
                      "password": "123",
                      "firstName": "",
                      "lastName": "Example",
                      "email": "invalid"
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
        assertThat(userRepository.count()).isZero();
    }

    @Test
    // Comprueba emision del JWT y que un endpoint protegido rechaza anonimos.
    void loginReturnsJwtAndProtectedAccountUsesAuthenticatedIdentity() throws Exception {
        register("alice", "alice@example.com");

        String response = mockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"ALICE","password":"secure-password"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id_token").isNotEmpty())
            .andReturn()
            .getResponse()
            .getContentAsString();
        Matcher tokenMatcher = Pattern.compile("\"id_token\"\\s*:\\s*\"([^\"]+)\"").matcher(response);
        assertThat(tokenMatcher.find()).isTrue();
        String token = tokenMatcher.group(1);

        mockMvc.perform(get("/api/account"))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/account").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.login").value("alice"))
            .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    // No se informa si el fallo fue el nombre de usuario o la contrasena.
    void loginRejectsIncorrectCredentials() throws Exception {
        register("alice", "alice@example.com");

        mockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"alice","password":"incorrect-password"}
                    """))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"));
    }

    @Test
    // Un parametro userId manipulable no cambia la identidad extraida del token.
    void accountIdentityComesFromJwtRatherThanClientSuppliedUserId() throws Exception {
        register("alice", "alice@example.com");
        register("bob", "bob@example.com");
        Long aliceId = userRepository.findByLogin("alice").orElseThrow().getId();
        Long bobId = userRepository.findByLogin("bob").orElseThrow().getId();
        String aliceToken = login("alice");

        mockMvc.perform(get("/api/account")
                .param("userId", bobId.toString())
                .header("Authorization", "Bearer " + aliceToken))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(aliceId))
            .andExpect(jsonPath("$.login").value("alice"));
    }

    @Test
    // Un token mal formado no autentica llamadas protegidas.
    void protectedEndpointRejectsInvalidJwt() throws Exception {
        mockMvc.perform(get("/api/account").header("Authorization", "Bearer invalid.token.value"))
            .andExpect(status().isUnauthorized());
    }

    private void register(String login, String email) throws Exception {
        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registrationJson(login, email)))
            .andExpect(status().isCreated());
    }

    private String login(String username) throws Exception {
        String response = mockMvc.perform(post("/api/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"username":"%s","password":"secure-password"}
                    """.formatted(username)))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        Matcher tokenMatcher = Pattern.compile("\"id_token\"\\s*:\\s*\"([^\"]+)\"").matcher(response);
        assertThat(tokenMatcher.find()).isTrue();
        return tokenMatcher.group(1);
    }

    private String registrationJson(String login, String email) {
        return """
            {
              "login": "%s",
              "password": "secure-password",
              "firstName": "Test",
              "lastName": "User",
              "email": "%s"
            }
            """.formatted(login, email);
    }
}
