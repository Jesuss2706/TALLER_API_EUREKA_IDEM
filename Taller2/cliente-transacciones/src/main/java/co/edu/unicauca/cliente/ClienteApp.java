package co.edu.unicauca.cliente;

import co.edu.unicauca.cliente.dto.ConsultaTransaccionesDTO;
import co.edu.unicauca.cliente.dto.RespuestaTransaccionDTO;
import co.edu.unicauca.cliente.dto.TokenDTO;
import co.edu.unicauca.cliente.dto.TransaccionDTO;
import co.edu.unicauca.cliente.excepciones.ErrorServidorException;
import co.edu.unicauca.cliente.excepciones.ServidorNoDisponibleException;
import co.edu.unicauca.cliente.servicios.ClienteTransaccionesRest;

import java.math.BigDecimal;
import java.util.Scanner;

/**
 * Cliente de consola. URL del servidor: primer argumento, variable de entorno SERVIDOR_URL
 * o http://localhost:8080 por defecto.
 */
public class ClienteApp {

    private final ClienteTransaccionesRest cliente;
    private final Scanner teclado = new Scanner(System.in);
    private String ultimoToken;

    public ClienteApp(ClienteTransaccionesRest cliente) {
        this.cliente = cliente;
    }

    public static void main(String[] args) {
        String url = args.length > 0 ? args[0]
                : System.getenv().getOrDefault("SERVIDOR_URL", "http://localhost:8080");
        System.out.println("Servidor de transacciones: " + url);
        new ClienteApp(new ClienteTransaccionesRest(url)).ejecutar();
    }

    private void ejecutar() {
        while (true) {
            System.out.println();
            System.out.println("=========== CLIENTE DE TRANSACCIONES ===========");
            System.out.println(" 1. Realizar transaccion (retiro)");
            System.out.println(" 2. Realizar transaccion con un token ya existente");
            System.out.println(" 3. Consultar transacciones");
            System.out.println(" 0. Salir");
            String opcion = leer("Opcion: ");
            switch (opcion) {
                case "1" -> realizarTransaccion();
                case "2" -> realizarTransaccionConToken();
                case "3" -> consultarTransacciones();
                case "0" -> {
                    System.out.println("Hasta luego");
                    return;
                }
                default -> System.out.println("Opcion no valida");
            }
        }
    }

    /** Obtiene internamente un token de idempotencia nuevo y realiza la transaccion. */
    private void realizarTransaccion() {
        String cedula = leer("Numero de cedula: ");
        BigDecimal monto = leerMonto();
        if (monto == null) {
            return;
        }
        try {
            TokenDTO token = cliente.obtenerToken(cedula);
            System.out.println("  Token de idempotencia obtenido: " + token.token());
            ultimoToken = token.token();
            enviarTransaccion(token.token(), monto);
        } catch (ServidorNoDisponibleException | ErrorServidorException e) {
            System.out.println(">> " + e.getMessage());
        }
    }

    /** Permite reutilizar un token para comprobar que el servidor no reejecuta la transaccion. */
    private void realizarTransaccionConToken() {
        String sugerencia = ultimoToken == null ? "" : " [Enter = ultimo token usado: " + ultimoToken + "]";
        String token = leer("Token de idempotencia" + sugerencia + ": ");
        if (token.isEmpty()) {
            if (ultimoToken == null) {
                System.out.println(">> Debe ingresar un token");
                return;
            }
            token = ultimoToken;
        }
        BigDecimal monto = leerMonto();
        if (monto == null) {
            return;
        }
        ultimoToken = token;
        try {
            enviarTransaccion(token, monto);
        } catch (ServidorNoDisponibleException | ErrorServidorException e) {
            System.out.println(">> " + e.getMessage());
        }
    }

    private void enviarTransaccion(String token, BigDecimal monto)
            throws ServidorNoDisponibleException, ErrorServidorException {
        RespuestaTransaccionDTO r = cliente.realizarTransaccion(token, monto);
        System.out.println();
        System.out.println("------------- RESULTADO -------------");
        System.out.println(" Token                     : " + r.token());
        System.out.println(" Monto solicitado          : " + r.montoSolicitado());
        System.out.println(" Transaccion realizada     : " + (r.realizada() ? "SI" : "NO"));
        System.out.println(" Realizada anteriormente   : " + (r.previamenteRealizada() ? "SI" : "NO"));
        System.out.println(" Valor de la transaccion   : " + r.montoTransaccion());
        System.out.println(" Fecha de la transaccion   : " + r.fechaTransaccion());
        System.out.println(" Mensaje                   : " + r.mensaje());
        System.out.println("-------------------------------------");
    }

    private void consultarTransacciones() {
        String cedula = leer("Numero de cedula: ");
        try {
            ConsultaTransaccionesDTO consulta = cliente.consultarTransacciones(cedula);
            System.out.println();
            System.out.println("Transacciones de la cedula " + consulta.cedula() + ": " + consulta.cantidadTransacciones());
            System.out.printf(" %-4s %-38s %-15s %s%n", "ID", "TOKEN DE IDEMPOTENCIA", "MONTO", "FECHA");
            for (TransaccionDTO t : consulta.transacciones()) {
                System.out.printf(" %-4d %-38s %-15s %s%n", t.id(), t.token(), t.monto(), t.fecha());
            }
            System.out.println("Tokens de idempotencia utilizados:");
            consulta.tokensUtilizados().forEach(t -> System.out.println("  - " + t));
        } catch (ServidorNoDisponibleException | ErrorServidorException e) {
            System.out.println(">> " + e.getMessage());
        }
    }

    private BigDecimal leerMonto() {
        String texto = leer("Monto a retirar: ");
        try {
            return new BigDecimal(texto.replace(",", "."));
        } catch (NumberFormatException e) {
            System.out.println(">> Monto no valido");
            return null;
        }
    }

    private String leer(String mensaje) {
        System.out.print(mensaje);
        return teclado.hasNextLine() ? teclado.nextLine().trim() : "0";
    }
}
