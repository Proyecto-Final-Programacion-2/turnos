package um.edu.ar.turnos_programacion2.infrastructure.web;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import um.edu.ar.turnos_programacion2.domain.user.exception.AccountConflictException;
import um.edu.ar.turnos_programacion2.domain.user.exception.InvalidCredentialsException;
import um.edu.ar.turnos_programacion2.domain.user.exception.UserAccountNotFoundException;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AccountConflictException.class)
    ResponseEntity<ProblemDetail> handleAccountConflict(AccountConflictException exception) {
        return conflict(exception.getCode(), exception.getField());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ProblemDetail> handleConcurrentAccountConflict(DataIntegrityViolationException exception) {
        // La base de datos sigue siendo la proteccion final ante dos registros simultaneos.
        String constraintName = findConstraintName(exception);
        if ("uk_jhi_user_login".equalsIgnoreCase(constraintName)) {
            return conflict("LOGIN_ALREADY_EXISTS", "login");
        }
        if ("uk_jhi_user_email".equalsIgnoreCase(constraintName)) {
            return conflict("EMAIL_ALREADY_EXISTS", "email");
        }
        // No disfraza violaciones de integridad no relacionadas como errores de duplicado.
        throw exception;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Uno o más campos no son válidos"
        );
        problem.setTitle("Validation error");
        problem.setProperty("code", "VALIDATION_ERROR");
        List<Map<String, String>> fieldErrors = exception.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(this::toFieldError)
            .toList();
        problem.setProperty("fieldErrors", fieldErrors);
        return ResponseEntity.badRequest().body(problem);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    ResponseEntity<ProblemDetail> handleInvalidCredentials() {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            "Credenciales incorrectas"
        );
        problem.setTitle("Unauthorized");
        problem.setProperty("code", "INVALID_CREDENTIALS");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(UserAccountNotFoundException.class)
    ResponseEntity<ProblemDetail> handleMissingAccount() {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            "Invalid access token"
        );
        problem.setTitle("Unauthorized");
        problem.setProperty("code", "INVALID_ACCESS_TOKEN");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    private Map<String, String> toFieldError(FieldError error) {
        return Map.of(
            "field", error.getField(),
            "message", error.getDefaultMessage() == null ? "invalid" : error.getDefaultMessage()
        );
    }

    private ResponseEntity<ProblemDetail> conflict(String code, String field) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT,
            "La cuenta ya existe"
        );
        problem.setTitle("Account conflict");
        problem.setType(URI.create("about:blank"));
        problem.setProperty("code", code);
        problem.setProperty("field", field);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    private String findConstraintName(Throwable exception) {
        // Hibernate puede envolver la violacion SQL en varias excepciones.
        for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException violation) {
                return violation.getConstraintName();
            }
        }
        return null;
    }
}
