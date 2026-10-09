package co.edu.unicauca.servidor.capaFachada.services;

import co.edu.unicauca.servidor.capaFachada.excepciones.ReglaNegocioException;

import java.math.BigDecimal;

final class Validaciones {

    private Validaciones() {
    }

    static String validarCedula(String cedula) {
        if (cedula == null || cedula.isBlank()) {
            throw new ReglaNegocioException("El numero de cedula es obligatorio");
        }
        String limpia = cedula.trim();
        if (!limpia.matches("\\d{5,15}")) {
            throw new ReglaNegocioException("El numero de cedula debe tener entre 5 y 15 digitos numericos");
        }
        return limpia;
    }

    static String validarToken(String token) {
        if (token == null || token.isBlank()) {
            throw new ReglaNegocioException("El token de idempotencia es obligatorio");
        }
        return token.trim();
    }

    static void validarMonto(BigDecimal monto) {
        if (monto == null) {
            throw new ReglaNegocioException("El monto a retirar es obligatorio");
        }
        if (monto.signum() <= 0) {
            throw new ReglaNegocioException("El monto a retirar debe ser mayor que cero");
        }
        if (monto.scale() > 2) {
            throw new ReglaNegocioException("El monto a retirar admite maximo 2 decimales");
        }
    }
}
