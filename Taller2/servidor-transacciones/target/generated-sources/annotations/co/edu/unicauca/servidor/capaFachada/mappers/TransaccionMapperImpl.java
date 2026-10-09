package co.edu.unicauca.servidor.capaFachada.mappers;

import co.edu.unicauca.servidor.capaAccesoADatos.models.TokenIdempotenciaEntity;
import co.edu.unicauca.servidor.capaAccesoADatos.models.TransaccionEntity;
import co.edu.unicauca.servidor.capaFachada.DTO.RespuestaTransaccionDTO;
import co.edu.unicauca.servidor.capaFachada.DTO.TransaccionDTO;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-10-08T18:04:36-0500",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.14 (Eclipse Adoptium)"
)
@Component
public class TransaccionMapperImpl implements TransaccionMapper {

    @Override
    public TransaccionDTO toDTO(TransaccionEntity entity) {
        if ( entity == null ) {
            return null;
        }

        String token = null;
        String cedula = null;
        Long id = null;
        BigDecimal monto = null;
        LocalDateTime fecha = null;

        token = entityTokenIdempotenciaToken( entity );
        cedula = entityTokenIdempotenciaCedula( entity );
        id = entity.getId();
        monto = entity.getMonto();
        fecha = entity.getFecha();

        TransaccionDTO transaccionDTO = new TransaccionDTO( id, token, cedula, monto, fecha );

        return transaccionDTO;
    }

    @Override
    public List<TransaccionDTO> toDTOs(List<TransaccionEntity> entities) {
        if ( entities == null ) {
            return null;
        }

        List<TransaccionDTO> list = new ArrayList<TransaccionDTO>( entities.size() );
        for ( TransaccionEntity transaccionEntity : entities ) {
            list.add( toDTO( transaccionEntity ) );
        }

        return list;
    }

    @Override
    public RespuestaTransaccionDTO toRespuesta(TransaccionEntity entity, BigDecimal montoSolicitado, boolean realizada, boolean previamenteRealizada, String mensaje) {
        if ( entity == null && montoSolicitado == null && mensaje == null ) {
            return null;
        }

        String token = null;
        BigDecimal montoTransaccion = null;
        LocalDateTime fechaTransaccion = null;
        if ( entity != null ) {
            token = entityTokenIdempotenciaToken( entity );
            montoTransaccion = entity.getMonto();
            fechaTransaccion = entity.getFecha();
        }
        BigDecimal montoSolicitado1 = null;
        montoSolicitado1 = montoSolicitado;
        boolean realizada1 = false;
        realizada1 = realizada;
        boolean previamenteRealizada1 = false;
        previamenteRealizada1 = previamenteRealizada;
        String mensaje1 = null;
        mensaje1 = mensaje;

        RespuestaTransaccionDTO respuestaTransaccionDTO = new RespuestaTransaccionDTO( token, montoSolicitado1, realizada1, previamenteRealizada1, montoTransaccion, fechaTransaccion, mensaje1 );

        return respuestaTransaccionDTO;
    }

    private String entityTokenIdempotenciaToken(TransaccionEntity transaccionEntity) {
        if ( transaccionEntity == null ) {
            return null;
        }
        TokenIdempotenciaEntity tokenIdempotencia = transaccionEntity.getTokenIdempotencia();
        if ( tokenIdempotencia == null ) {
            return null;
        }
        String token = tokenIdempotencia.getToken();
        if ( token == null ) {
            return null;
        }
        return token;
    }

    private String entityTokenIdempotenciaCedula(TransaccionEntity transaccionEntity) {
        if ( transaccionEntity == null ) {
            return null;
        }
        TokenIdempotenciaEntity tokenIdempotencia = transaccionEntity.getTokenIdempotencia();
        if ( tokenIdempotencia == null ) {
            return null;
        }
        String cedula = tokenIdempotencia.getCedula();
        if ( cedula == null ) {
            return null;
        }
        return cedula;
    }
}
