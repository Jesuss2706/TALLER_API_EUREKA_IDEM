package co.edu.unicauca.servidor.capaControladores.simulacion;

import co.edu.unicauca.servidor.capaFachada.excepciones.FalloSimuladoException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simula fallos de comunicacion sobre el servicio de transacciones.
 *
 * Un mensaje "perdido" se simula reteniendo la peticion durante RETARDO_FALLO_MS (mayor que el
 * timeout de 3 s del cliente), de modo que el cliente nunca recibe la respuesta a tiempo.
 *
 * - Fallo en la peticion: la peticion se retiene y se descarta SIN procesar la transaccion.
 * - Fallo en la respuesta: la transaccion SI se procesa y se guarda, pero la respuesta se retiene.
 *
 * Los intentos se cuentan por token de idempotencia. Con REINTENTOS_PARA_RESPONDER = N fallan los
 * intentos 1..N (el intento inicial y los primeros N-1 reintentos) y el servidor responde en el reintento N.
 */
@Component
public class SimuladorFallos {

    private static final Logger log = LoggerFactory.getLogger(SimuladorFallos.class);

    private final boolean falloPeticion;
    private final boolean falloRespuesta;
    private final int reintentosParaResponder;
    private final long retardoMs;

    private final Map<String, AtomicInteger> intentosPorToken = new ConcurrentHashMap<>();

    public SimuladorFallos(@Value("${simulacion.fallo-peticion}") boolean falloPeticion,
                           @Value("${simulacion.fallo-respuesta}") boolean falloRespuesta,
                           @Value("${simulacion.reintentos-para-responder}") int reintentosParaResponder,
                           @Value("${simulacion.retardo-ms}") long retardoMs) {
        this.falloPeticion = falloPeticion;
        this.falloRespuesta = falloRespuesta;
        this.reintentosParaResponder = Math.max(0, reintentosParaResponder);
        this.retardoMs = retardoMs;
    }

    @PostConstruct
    void mostrarConfiguracion() {
        log.info("================ SIMULACION DE FALLOS ================");
        log.info(" SIMULAR_FALLO_PETICION    = {}", falloPeticion);
        log.info(" SIMULAR_FALLO_RESPUESTA   = {}", falloRespuesta);
        log.info(" REINTENTOS_PARA_RESPONDER = {}", reintentosParaResponder);
        log.info(" RETARDO_FALLO_MS          = {}", retardoMs);
        if (!falloPeticion && !falloRespuesta) {
            log.info(" -> Sin simulacion: el servidor responde al primer intento");
        } else if (reintentosParaResponder == 0) {
            log.warn(" -> ATENCION: hay un fallo activado pero REINTENTOS_PARA_RESPONDER = 0, asi que ningun intento falla");
        } else {
            log.info(" -> Fallan los primeros {} intento(s) de cada token; responde en el intento #{} (reintento {})",
                    reintentosParaResponder, reintentosParaResponder + 1, reintentosParaResponder);
        }
        log.info("======================================================");
    }

    /** Registra la llegada de una peticion y retorna el numero de intento (1 = intento inicial). */
    public int registrarIntento(String token) {
        int intento = intentosPorToken.computeIfAbsent(String.valueOf(token), k -> new AtomicInteger()).incrementAndGet();
        log.info("Peticion recibida: token={} intento #{} ({})", token, intento,
                intento == 1 ? "intento inicial" : "reintento " + (intento - 1));
        return intento;
    }

    /** Se invoca ANTES de procesar la transaccion. */
    public void simularFalloPeticion(String token, int intento) {
        if (falloPeticion && intento <= reintentosParaResponder) {
            log.warn("[SIMULACION] Intento #{} token={}: se PIERDE EL MENSAJE DE PETICION. La transaccion no se procesa", intento, token);
            retener();
            throw new FalloSimuladoException("Fallo simulado en el mensaje de peticion (intento #" + intento + ")");
        }
    }

    /** Se invoca DESPUES de procesar la transaccion y antes de enviar la respuesta. */
    public void simularFalloRespuesta(String token, int intento) {
        if (falloRespuesta && intento <= reintentosParaResponder) {
            log.warn("[SIMULACION] Intento #{} token={}: transaccion procesada, pero se PIERDE EL MENSAJE DE RESPUESTA", intento, token);
            retener();
        }
    }

    private void retener() {
        try {
            Thread.sleep(retardoMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
