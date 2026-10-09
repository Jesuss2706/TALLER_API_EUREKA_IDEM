package co.edu.unicauca.servidor.capaFachada.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Respuesta del servicio 2.
 *
 * @param token                token de idempotencia de la peticion
 * @param montoSolicitado      monto enviado en esta peticion
 * @param realizada            true si la transaccion se ejecuto en ESTA peticion
 * @param previamenteRealizada true si el token ya habia sido utilizado (la peticion no se reejecuto)
 * @param montoTransaccion     monto de la transaccion registrada en el historial para ese token
 * @param fechaTransaccion     fecha de la transaccion registrada en el historial
 * @param mensaje              descripcion del resultado
 */
public record RespuestaTransaccionDTO(String token,
                                      BigDecimal montoSolicitado,
                                      boolean realizada,
                                      boolean previamenteRealizada,
                                      BigDecimal montoTransaccion,
                                      LocalDateTime fechaTransaccion,
                                      String mensaje) {
}
