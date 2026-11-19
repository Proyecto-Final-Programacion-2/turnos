package um.edu.ar.turnos_programacion2.domain.user.exception;

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
