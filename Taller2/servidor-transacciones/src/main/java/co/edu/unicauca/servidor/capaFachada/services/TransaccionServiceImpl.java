package co.edu.unicauca.servidor.capaFachada.services;

import co.edu.unicauca.servidor.capaAccesoADatos.models.TokenIdempotenciaEntity;
import co.edu.unicauca.servidor.capaAccesoADatos.models.TransaccionEntity;
import co.edu.unicauca.servidor.capaAccesoADatos.repositories.TokenIdempotenciaRepository;
import co.edu.unicauca.servidor.capaAccesoADatos.repositories.TransaccionRepository;
import co.edu.unicauca.servidor.capaFachada.DTO.ConsultaTransaccionesDTO;
import co.edu.unicauca.servidor.capaFachada.DTO.RespuestaTransaccionDTO;
import co.edu.unicauca.servidor.capaFachada.DTO.SolicitudTransaccionDTO;
import co.edu.unicauca.servidor.capaFachada.DTO.TransaccionDTO;
import co.edu.unicauca.servidor.capaFachada.excepciones.EntidadNoExisteException;
import co.edu.unicauca.servidor.capaFachada.excepciones.ReglaNegocioException;
import co.edu.unicauca.servidor.capaFachada.mappers.TransaccionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransaccionServiceImpl implements ITransaccionService {

    private static final Logger log = LoggerFactory.getLogger(TransaccionServiceImpl.class);

    private final TokenIdempotenciaRepository tokenRepository;
    private final TransaccionRepository transaccionRepository;
    private final TransaccionMapper transaccionMapper;

    public TransaccionServiceImpl(TokenIdempotenciaRepository tokenRepository,
                                  TransaccionRepository transaccionRepository,
                                  TransaccionMapper transaccionMapper) {
        this.tokenRepository = tokenRepository;
        this.transaccionRepository = transaccionRepository;
        this.transaccionMapper = transaccionMapper;
    }

    @Override
    public RespuestaTransaccionDTO realizarTransaccion(SolicitudTransaccionDTO solicitud) {
        if (solicitud == null) {
            throw new ReglaNegocioException("La peticion no tiene cuerpo");
        }
        String token = Validaciones.validarToken(solicitud.token());
        BigDecimal monto = solicitud.monto();
        Validaciones.validarMonto(monto);

        TokenIdempotenciaEntity tokenEntity = tokenRepository.findById(token)
                .orElseThrow(() -> new EntidadNoExisteException(
                        "El token de idempotencia " + token + " no existe. Solicite un token antes de realizar la transaccion"));

        // Antes de ejecutar, siempre se verifica si el token ya fue utilizado
        Optional<TransaccionEntity> previa = transaccionRepository.findByTokenIdempotenciaToken(token);
        if (previa.isPresent()) {
            return respuestaPreviamenteRealizada(previa.get(), monto);
        }

        TransaccionEntity nueva = new TransaccionEntity(tokenEntity, monto, LocalDateTime.now());
        try {
            transaccionRepository.saveAndFlush(nueva);
        } catch (DataIntegrityViolationException e) {
            // Dos peticiones con el mismo token llegaron al mismo tiempo: la restriccion UNIQUE
            // del token garantiza que solo una se registre; la otra se trata como reintento.
            TransaccionEntity registrada = transaccionRepository.findByTokenIdempotenciaToken(token)
                    .orElseThrow(() -> e);
            return respuestaPreviamenteRealizada(registrada, monto);
        }

        log.info("Transaccion REALIZADA. token={} cedula={} monto={}", token, tokenEntity.getCedula(), monto);
        return transaccionMapper.toRespuesta(nueva, monto, true, false,
                "Transaccion realizada exitosamente");
    }

    private RespuestaTransaccionDTO respuestaPreviamenteRealizada(TransaccionEntity previa, BigDecimal montoSolicitado) {
        log.warn("Transaccion NO reejecutada: el token {} ya fue utilizado (monto registrado={})",
                previa.getTokenIdempotencia().getToken(), previa.getMonto());
        return transaccionMapper.toRespuesta(previa, montoSolicitado, false, true,
                "La transaccion no se ejecuto de nuevo: el token de idempotencia ya habia sido utilizado "
                        + "en una transaccion por valor de " + previa.getMonto());
    }

    @Override
    public ConsultaTransaccionesDTO consultarTransacciones(String cedula) {
        String cedulaValida = Validaciones.validarCedula(cedula);

        List<TransaccionDTO> transacciones = transaccionMapper.toDTOs(
                transaccionRepository.findByTokenIdempotenciaCedulaOrderByFechaAsc(cedulaValida));
        List<String> tokensUtilizados = transacciones.stream()
                .map(TransaccionDTO::token)
                .toList();

        return new ConsultaTransaccionesDTO(cedulaValida, transacciones.size(), transacciones, tokensUtilizados);
    }
}
