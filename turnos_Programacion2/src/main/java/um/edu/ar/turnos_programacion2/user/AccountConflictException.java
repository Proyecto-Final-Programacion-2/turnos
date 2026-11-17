package um.edu.ar.turnos_programacion2.user;

// Error funcional que conserva un codigo y el campo duplicado para la respuesta REST.
public class AccountConflictException extends RuntimeException {

    private final String code;
    private final String field;

    public AccountConflictException(String code, String field) {
        super(code);
        this.code = code;
        this.field = field;
    }

    public String getCode() {
        return code;
    }

    public String getField() {
        return field;
    }
}
