package co.edu.unicauca.servidor.capaFachada.services;

import co.edu.unicauca.servidor.capaFachada.DTO.SolicitudTokenDTO;
import co.edu.unicauca.servidor.capaFachada.DTO.TokenDTO;

public interface ITokenService {

    TokenDTO generarToken(SolicitudTokenDTO solicitud);
}
