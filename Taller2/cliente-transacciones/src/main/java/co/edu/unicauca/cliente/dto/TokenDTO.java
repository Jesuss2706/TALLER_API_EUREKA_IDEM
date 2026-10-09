package co.edu.unicauca.cliente.dto;

import java.time.LocalDateTime;

public record TokenDTO(String token, String cedula, LocalDateTime fechaCreacion) {
}
