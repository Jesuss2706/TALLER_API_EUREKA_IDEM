package co.edu.unicauca.servidor.capaFachada.excepciones;

/** El recurso solicitado no existe (HTTP 404). */
public class EntidadNoExisteException extends RuntimeException {
    public EntidadNoExisteException(String mensaje) {
        super(mensaje);
    }
}
