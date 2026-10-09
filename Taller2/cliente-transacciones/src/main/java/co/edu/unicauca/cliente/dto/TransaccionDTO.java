package co.edu.unicauca.cliente.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransaccionDTO(Long id, String token, String cedula, BigDecimal monto, LocalDateTime fecha) {
}
