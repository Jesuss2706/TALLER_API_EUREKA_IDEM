package co.edu.unicauca.servidor.capaFachada.mappers;

import co.edu.unicauca.servidor.capaAccesoADatos.models.TransaccionEntity;
import co.edu.unicauca.servidor.capaFachada.DTO.RespuestaTransaccionDTO;
import co.edu.unicauca.servidor.capaFachada.DTO.TransaccionDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface TransaccionMapper {

    @Mapping(source = "tokenIdempotencia.token", target = "token")
    @Mapping(source = "tokenIdempotencia.cedula", target = "cedula")
    TransaccionDTO toDTO(TransaccionEntity entity);

    List<TransaccionDTO> toDTOs(List<TransaccionEntity> entities);

    @Mapping(source = "entity.tokenIdempotencia.token", target = "token")
    @Mapping(source = "entity.monto", target = "montoTransaccion")
    @Mapping(source = "entity.fecha", target = "fechaTransaccion")
    RespuestaTransaccionDTO toRespuesta(TransaccionEntity entity,
                                        BigDecimal montoSolicitado,
                                        boolean realizada,
                                        boolean previamenteRealizada,
                                        String mensaje);
}
