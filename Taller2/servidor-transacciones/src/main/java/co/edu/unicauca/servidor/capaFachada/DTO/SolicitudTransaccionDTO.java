package co.edu.unicauca.servidor.capaFachada.DTO;

import java.math.BigDecimal;

/** Peticion del servicio 2: token de idempotencia y monto a retirar. */
public record SolicitudTransaccionDTO(String token, BigDecimal monto) {
}
