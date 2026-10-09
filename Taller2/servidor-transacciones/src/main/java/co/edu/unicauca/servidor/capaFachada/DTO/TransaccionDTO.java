package co.edu.unicauca.servidor.capaFachada.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** Registro del historial de transacciones. */
public record TransaccionDTO(Long id, String token, String cedula, BigDecimal monto, LocalDateTime fecha) {
}
