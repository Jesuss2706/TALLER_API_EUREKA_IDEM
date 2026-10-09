package co.edu.unicauca.servidor.capaFachada.mappers;

import co.edu.unicauca.servidor.capaAccesoADatos.models.TokenIdempotenciaEntity;
import co.edu.unicauca.servidor.capaFachada.DTO.TokenDTO;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-08T18:04:36-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.14 (Eclipse Adoptium)"
)
@Component
public class TokenMapperImpl implements TokenMapper {

    @Override
    public TokenDTO toDTO(TokenIdempotenciaEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String token = null;
        String cedula = null;
        LocalDateTime fechaCreacion = null;

        token = entity.getToken();
        cedula = entity.getCedula();
        fechaCreacion = entity.getFechaCreacion();

        TokenDTO tokenDTO = new TokenDTO( token, cedula, fechaCreacion );

        return tokenDTO;
    }
}
