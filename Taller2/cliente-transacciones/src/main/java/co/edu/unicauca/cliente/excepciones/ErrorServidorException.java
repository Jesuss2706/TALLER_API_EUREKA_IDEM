package co.edu.unicauca.cliente.excepciones;

/** El servidor respondio, pero rechazo la peticion (datos invalidos, token inexistente, etc.). */
public class ErrorServidorException extends Exception {
    public ErrorServidorException(String mensaje) {
        super(mensaje);
    }
}
