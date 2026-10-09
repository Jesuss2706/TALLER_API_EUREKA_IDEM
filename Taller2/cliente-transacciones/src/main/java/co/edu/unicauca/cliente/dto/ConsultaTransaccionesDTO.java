package co.edu.unicauca.cliente.dto;

import java.util.List;

public record ConsultaTransaccionesDTO(String cedula,
                                       int cantidadTransacciones,
                                       List<TransaccionDTO> transacciones,
                                       List<String> tokensUtilizados) {
}
