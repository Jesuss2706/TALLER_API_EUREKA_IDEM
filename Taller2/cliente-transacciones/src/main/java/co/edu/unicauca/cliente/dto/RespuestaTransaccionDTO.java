package co.edu.unicauca.cliente.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RespuestaTransaccionDTO(String token,
                                      BigDecimal montoSolicitado,
                                      boolean realizada,
                                      boolean previamenteRealizada,
                                      BigDecimal montoTransaccion,
                                      LocalDateTime fechaTransaccion,
                                      String mensaje) {
}
