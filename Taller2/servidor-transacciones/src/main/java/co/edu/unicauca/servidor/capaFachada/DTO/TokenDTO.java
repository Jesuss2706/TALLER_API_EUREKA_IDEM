package co.edu.unicauca.servidor.capaFachada.DTO;

import java.time.LocalDateTime;

/** Respuesta del servicio 1: token de idempotencia generado. */
public record TokenDTO(String token, String cedula, LocalDateTime fechaCreacion) {
}
