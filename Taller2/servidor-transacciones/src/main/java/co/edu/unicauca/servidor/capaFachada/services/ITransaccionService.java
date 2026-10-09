package co.edu.unicauca.servidor.capaFachada.services;

import co.edu.unicauca.servidor.capaFachada.DTO.ConsultaTransaccionesDTO;
import co.edu.unicauca.servidor.capaFachada.DTO.RespuestaTransaccionDTO;
import co.edu.unicauca.servidor.capaFachada.DTO.SolicitudTransaccionDTO;

public interface ITransaccionService {

    RespuestaTransaccionDTO realizarTransaccion(SolicitudTransaccionDTO solicitud);

    ConsultaTransaccionesDTO consultarTransacciones(String cedula);
}
