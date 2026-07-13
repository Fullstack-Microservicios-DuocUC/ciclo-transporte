package cl.duoc.mineria.ciclo_transporte.exception;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public GlobalExceptionHandler() {
        System.out.println("GlobalExceptionHandler (CicloTransporte) SE HA REGISTRADO CORRECTAMENTE");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(MethodArgumentNotValidException ex) {
        System.out.println("[Ciclo Error] - Errores de validación detectados en el DTO");

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Error de validación en los datos enviados al microservicio");

        problem.setTitle("Validation Error");
        problem.setProperty("timestamp", Instant.now());

        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Valor inválido"));

        problem.setProperty("error", errors);

        System.out.println("Campos rechazados: " + errors);
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleJsonParseError(HttpMessageNotReadableException ex) {
        System.out.println("9472 Error de parseo JSON capturado (Jackson)");

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Error al procesar el cuerpo JSON enviado");

        problem.setTitle("JSON Parse Error");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("detalle", ex.getMostSpecificCause().getMessage());
        return problem;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        System.out.println("Recurso no encontrado: " + ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage());

        problem.setTitle("Resource not Found");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(CicloValidationException.class)
    public ProblemDetail handleCicloValidation(CicloValidationException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage());

        problem.setTitle("Iniciación de Ciclo Rechazada");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {
        System.out.println("EXCEPCIÓN NO CONTROLADA EN EL SISTEMA: " + ex.getClass().getName());
        ex.printStackTrace();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor en el módulo de transporte");

        problem.setTitle("Internal Server Error");
        problem.setProperty("timestamp", Instant.now());
        problem.setProperty("detalle", ex.getMessage());
        problem.setProperty("tipoExcepcion", ex.getClass().getSimpleName());
        return problem;
    }

    @ExceptionHandler(ServicioExternoNoDisponibleException.class)
    public ProblemDetail handleServicioExternoNoDisponible(ServicioExternoNoDisponibleException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        problem.setTitle("Dependencia externa no disponible");
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }

    @ExceptionHandler(TurnoInvalidoException.class)
    public ResponseEntity<ProblemDetail> handleTurnoInvalidoException(TurnoInvalidoException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage());
        problemDetail.setTitle("Turno Inválido o No Existe");
        problemDetail.setInstance(URI.create(request.getDescription(false).replace("uri=", "")));
        problemDetail.setProperty("timestamp", Instant.now());
        return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
    }

}