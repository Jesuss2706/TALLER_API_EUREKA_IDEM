package co.edu.unicauca.servidor.capaFachada.DTO;

import java.util.List;

/** Respuesta del servicio 3: transacciones realizadas y tokens de idempotencia utilizados. */
public record ConsultaTransaccionesDTO(String cedula,
                                       int cantidadTransacciones,
                                       List<TransaccionDTO> transacciones,
                                       List<String> tokensUtilizados) {
}
