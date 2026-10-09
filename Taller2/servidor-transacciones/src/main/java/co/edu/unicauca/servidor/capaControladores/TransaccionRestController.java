package co.edu.unicauca.servidor.capaControladores;

import co.edu.unicauca.servidor.capaControladores.simulacion.SimuladorFallos;
import co.edu.unicauca.servidor.capaFachada.DTO.ConsultaTransaccionesDTO;
import co.edu.unicauca.servidor.capaFachada.DTO.RespuestaTransaccionDTO;
import co.edu.unicauca.servidor.capaFachada.DTO.SolicitudTransaccionDTO;
import co.edu.unicauca.servidor.capaFachada.services.ITransaccionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transacciones")
public class TransaccionRestController {

    private final ITransaccionService transaccionService;
    private final SimuladorFallos simuladorFallos;

    public TransaccionRestController(ITransaccionService transaccionService, SimuladorFallos simuladorFallos) {
        this.transaccionService = transaccionService;
        this.simuladorFallos = simuladorFallos;
    }

    /** Servicio REST 2: realizar una transaccion (retiro) usando un token de idempotencia. */
    @PostMapping
    public ResponseEntity<RespuestaTransaccionDTO> realizarTransaccion(@RequestBody SolicitudTransaccionDTO solicitud) {
        String token = solicitud == null ? null : solicitud.token();
        int intento = simuladorFallos.registrarIntento(token);

        simuladorFallos.simularFalloPeticion(token, intento);
        RespuestaTransaccionDTO respuesta = transaccionService.realizarTransaccion(solicitud);
        simuladorFallos.simularFalloRespuesta(token, intento);

        return ResponseEntity.ok(respuesta);
    }

    /** Servicio REST 3: consultar las transacciones y tokens utilizados por una cedula. */
    @GetMapping("/{cedula}")
    public ResponseEntity<ConsultaTransaccionesDTO> consultarTransacciones(@PathVariable String cedula) {
        return ResponseEntity.ok(transaccionService.consultarTransacciones(cedula));
    }
}
