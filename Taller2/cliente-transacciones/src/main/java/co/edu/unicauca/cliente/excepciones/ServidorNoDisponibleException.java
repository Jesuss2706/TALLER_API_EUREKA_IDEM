package co.edu.unicauca.cliente.excepciones;

/** El servidor no respondio despues de agotar todos los reintentos. */
public class ServidorNoDisponibleException extends Exception {
    public ServidorNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
