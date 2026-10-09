package co.edu.unicauca.servidor.capaControladores;

import co.edu.unicauca.servidor.capaFachada.DTO.SolicitudTokenDTO;
import co.edu.unicauca.servidor.capaFachada.DTO.TokenDTO;
import co.edu.unicauca.servidor.capaFachada.services.ITokenService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tokens")
public class TokenRestController {

    private final ITokenService tokenService;

    public TokenRestController(ITokenService tokenService) {
        this.tokenService = tokenService;
    }

    /** Servicio REST 1: generar token de idempotencia a partir del numero de cedula. */
    @PostMapping
    public ResponseEntity<TokenDTO> generarToken(@RequestBody SolicitudTokenDTO solicitud) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tokenService.generarToken(solicitud));
    }
}
