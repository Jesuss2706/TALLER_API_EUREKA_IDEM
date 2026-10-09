package co.edu.unicauca.servidor.capaFachada.mappers;

import co.edu.unicauca.servidor.capaAccesoADatos.models.TokenIdempotenciaEntity;
import co.edu.unicauca.servidor.capaFachada.DTO.TokenDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TokenMapper {

    TokenDTO toDTO(TokenIdempotenciaEntity entity);
}
