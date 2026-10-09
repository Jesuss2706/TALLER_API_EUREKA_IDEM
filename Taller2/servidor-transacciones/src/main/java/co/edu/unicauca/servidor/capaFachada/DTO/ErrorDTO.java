package co.edu.unicauca.servidor.capaFachada.DTO;

import java.time.LocalDateTime;

public record ErrorDTO(int codigoHttp, String mensaje, LocalDateTime fecha) {
}
