package co.edu.unicauca.cliente.servicios;

import co.edu.unicauca.cliente.dto.ConsultaTransaccionesDTO;
import co.edu.unicauca.cliente.dto.ErrorDTO;
import co.edu.unicauca.cliente.dto.RespuestaTransaccionDTO;
import co.edu.unicauca.cliente.dto.TokenDTO;
import co.edu.unicauca.cliente.excepciones.ErrorServidorException;
import co.edu.unicauca.cliente.excepciones.ServidorNoDisponibleException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Consume los servicios REST del servidor de transacciones.
 *
 * Politica de reintentos para realizar una transaccion:
 * - Si el servidor no responde en 3 segundos, se espera 2 segundos y se reenvia la solicitud
 *   con el MISMO token de idempotencia.
 * - Se hacen maximo 4 reintentos (5 envios en total). Si el 4 reintento falla, el servidor
 *   se considera no disponible.
 */
public class ClienteTransaccionesRest {

    private static final Duration TIEMPO_MAXIMO_RESPUESTA = Duration.ofSeconds(3);
    private static final long ESPERA_ENTRE_REINTENTOS_MS = 2000;
    private static final int MAXIMO_REINTENTOS = 4;
    private static final DateTimeFormatter HORA = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private final String urlServidor;
    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public ClienteTransaccionesRest(String urlServidor) {
        this.urlServidor = urlServidor.endsWith("/") ? urlServidor.substring(0, urlServidor.length() - 1) : urlServidor;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(TIEMPO_MAXIMO_RESPUESTA)
                .build();
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /** Servicio 1: obtiene un token de idempotencia para la cedula (un solo intento). */
    public TokenDTO obtenerToken(String cedula) throws ServidorNoDisponibleException, ErrorServidorException {
        HttpRequest peticion = HttpRequest.newBuilder(URI.create(urlServidor + "/api/tokens"))
                .timeout(TIEMPO_MAXIMO_RESPUESTA)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(aJson(Map.of("cedula", cedula))))
                .build();
        try {
            HttpResponse<String> respuesta = httpClient.send(peticion, HttpResponse.BodyHandlers.ofString());
            return leer(respuesta, TokenDTO.class);
        } catch (IOException e) {
            throw new ServidorNoDisponibleException("No fue posible obtener el token de idempotencia: " + descripcion(e));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServidorNoDisponibleException("Operacion interrumpida");
        }
    }

    /** Servicio 2: realiza la transaccion con reintentos usando siempre el mismo token. */
    public RespuestaTransaccionDTO realizarTransaccion(String token, BigDecimal monto)
            throws ServidorNoDisponibleException, ErrorServidorException {
        HttpRequest peticion = HttpRequest.newBuilder(URI.create(urlServidor + "/api/transacciones"))
                .timeout(TIEMPO_MAXIMO_RESPUESTA)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(aJson(Map.of("token", token, "monto", monto))))
                .build();

        for (int reintento = 0; reintento <= MAXIMO_REINTENTOS; reintento++) {
            if (reintento == 0) {
                traza("Enviando solicitud (intento inicial) con token " + token);
            } else {
                traza("Reintento " + reintento + " de " + MAXIMO_REINTENTOS + ": reenviando solicitud con el MISMO token " + token);
            }

            try {
                HttpResponse<String> respuesta = httpClient.send(peticion, HttpResponse.BodyHandlers.ofString());
                if (respuesta.statusCode() < 500) {
                    traza("Respuesta recibida (HTTP " + respuesta.statusCode() + ")");
                    return leer(respuesta, RespuestaTransaccionDTO.class);
                }
                traza("El servidor respondio con error HTTP " + respuesta.statusCode());
            } catch (HttpTimeoutException e) {
                traza("El servidor no respondio en " + TIEMPO_MAXIMO_RESPUESTA.toSeconds() + " segundos");
            } catch (IOException e) {
                traza("Fallo de comunicacion: " + descripcion(e));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ServidorNoDisponibleException("Operacion interrumpida");
            }

            if (reintento < MAXIMO_REINTENTOS) {
                traza("Esperando " + ESPERA_ENTRE_REINTENTOS_MS / 1000 + " segundos para reintentar...");
                esperar();
            }
        }

        throw new ServidorNoDisponibleException("No fue posible acceder al servidor: no respondio despues de "
                + MAXIMO_REINTENTOS + " reintentos");
    }

    /** Servicio 3: consulta las transacciones de una cedula (un solo intento). */
    public ConsultaTransaccionesDTO consultarTransacciones(String cedula)
            throws ServidorNoDisponibleException, ErrorServidorException {
        String cedulaUrl = URLEncoder.encode(cedula, StandardCharsets.UTF_8);
        HttpRequest peticion = HttpRequest.newBuilder(URI.create(urlServidor + "/api/transacciones/" + cedulaUrl))
                .timeout(TIEMPO_MAXIMO_RESPUESTA)
                .GET()
                .build();
        try {
            HttpResponse<String> respuesta = httpClient.send(peticion, HttpResponse.BodyHandlers.ofString());
            return leer(respuesta, ConsultaTransaccionesDTO.class);
        } catch (IOException e) {
            throw new ServidorNoDisponibleException("No fue posible consultar las transacciones: " + descripcion(e));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServidorNoDisponibleException("Operacion interrumpida");
        }
    }

    private <T> T leer(HttpResponse<String> respuesta, Class<T> tipo) throws ErrorServidorException, IOException {
        if (respuesta.statusCode() >= 200 && respuesta.statusCode() < 300) {
            return mapper.readValue(respuesta.body(), tipo);
        }
        String mensaje;
        try {
            mensaje = mapper.readValue(respuesta.body(), ErrorDTO.class).mensaje();
        } catch (IOException e) {
            mensaje = respuesta.body();
        }
        throw new ErrorServidorException("HTTP " + respuesta.statusCode() + ": " + mensaje);
    }

    private String aJson(Object objeto) {
        try {
            return mapper.writeValueAsString(objeto);
        } catch (IOException e) {
            throw new IllegalStateException("No fue posible convertir la peticion a JSON", e);
        }
    }

    private static String descripcion(IOException e) {
        if (e instanceof HttpTimeoutException) {
            return "el servidor no respondio en " + TIEMPO_MAXIMO_RESPUESTA.toSeconds() + " segundos";
        }
        return e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
    }

    private static void esperar() {
        try {
            Thread.sleep(ESPERA_ENTRE_REINTENTOS_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void traza(String mensaje) {
        System.out.println("  [" + LocalTime.now().format(HORA) + "] " + mensaje);
    }
}
