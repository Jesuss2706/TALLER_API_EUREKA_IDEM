package co.edu.unicauca.servidor.capaFachada.excepciones;

/** Error de validacion de los datos recibidos (HTTP 400). */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
