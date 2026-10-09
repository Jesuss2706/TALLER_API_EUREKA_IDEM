package co.edu.unicauca.servidor.capaControladores;

import co.edu.unicauca.servidor.capaFachada.DTO.ErrorDTO;
import co.edu.unicauca.servidor.capaFachada.excepciones.EntidadNoExisteException;
import co.edu.unicauca.servidor.capaFachada.excepciones.FalloSimuladoException;
import co.edu.unicauca.servidor.capaFachada.excepciones.ReglaNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class ManejadorExcepciones {

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorDTO> reglaNegocio(ReglaNegocioException e) {
        return error(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDTO> cuerpoInvalido(HttpMessageNotReadableException e) {
        return error(HttpStatus.BAD_REQUEST, "El cuerpo de la peticion no es un JSON valido");
    }

    @ExceptionHandler(EntidadNoExisteException.class)
    public ResponseEntity<ErrorDTO> noExiste(EntidadNoExisteException e) {
        return error(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(FalloSimuladoException.class)
    public ResponseEntity<ErrorDTO> falloSimulado(FalloSimuladoException e) {
        return error(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    private ResponseEntity<ErrorDTO> error(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(new ErrorDTO(estado.value(), mensaje, LocalDateTime.now()));
    }
}
