package co.edu.unicauca.cliente.dto;

import java.time.LocalDateTime;

public record ErrorDTO(int codigoHttp, String mensaje, LocalDateTime fecha) {
}
