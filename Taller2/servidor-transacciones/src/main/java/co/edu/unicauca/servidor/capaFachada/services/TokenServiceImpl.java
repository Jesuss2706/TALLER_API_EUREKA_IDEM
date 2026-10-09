package co.edu.unicauca.servidor.capaFachada.services;

import co.edu.unicauca.servidor.capaAccesoADatos.models.TokenIdempotenciaEntity;
import co.edu.unicauca.servidor.capaAccesoADatos.repositories.TokenIdempotenciaRepository;
import co.edu.unicauca.servidor.capaFachada.DTO.SolicitudTokenDTO;
import co.edu.unicauca.servidor.capaFachada.DTO.TokenDTO;
import co.edu.unicauca.servidor.capaFachada.excepciones.ReglaNegocioException;
import co.edu.unicauca.servidor.capaFachada.mappers.TokenMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TokenServiceImpl implements ITokenService {

    private static final Logger log = LoggerFactory.getLogger(TokenServiceImpl.class);

    private final TokenIdempotenciaRepository tokenRepository;
    private final TokenMapper tokenMapper;

    public TokenServiceImpl(TokenIdempotenciaRepository tokenRepository, TokenMapper tokenMapper) {
        this.tokenRepository = tokenRepository;
        this.tokenMapper = tokenMapper;
    }

    @Override
    public TokenDTO generarToken(SolicitudTokenDTO solicitud) {
        if (solicitud == null) {
            throw new ReglaNegocioException("La peticion no tiene cuerpo");
        }
        String cedula = Validaciones.validarCedula(solicitud.cedula());

        TokenIdempotenciaEntity token = new TokenIdempotenciaEntity(
                UUID.randomUUID().toString(), cedula, LocalDateTime.now());
        tokenRepository.save(token);

        log.info("Token de idempotencia generado para cedula {}: {}", cedula, token.getToken());
        return tokenMapper.toDTO(token);
    }
}
