package co.edu.unicauca.servidor.capaFachada.excepciones;

/** Fallo de comunicacion simulado (HTTP 503). El cliente normalmente ya no espera esta respuesta. */
public class FalloSimuladoException extends RuntimeException {
    public FalloSimuladoException(String mensaje) {
        super(mensaje);
    }
}
