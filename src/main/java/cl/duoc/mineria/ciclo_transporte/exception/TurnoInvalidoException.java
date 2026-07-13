package cl.duoc.mineria.ciclo_transporte.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TurnoInvalidoException extends RuntimeException {
    public TurnoInvalidoException(String message) {
        super(message);
    }
}