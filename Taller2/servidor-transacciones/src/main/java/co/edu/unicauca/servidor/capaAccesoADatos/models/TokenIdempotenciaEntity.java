package co.edu.unicauca.servidor.capaAccesoADatos.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "tokens_idempotencia")
public class TokenIdempotenciaEntity {

    @Id
    @Column(length = 36)
    private String token;

    @Column(nullable = false, length = 15)
    private String cedula;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    public TokenIdempotenciaEntity() {
    }

    public TokenIdempotenciaEntity(String token, String cedula, LocalDateTime fechaCreacion) {
        this.token = token;
        this.cedula = cedula;
        this.fechaCreacion = fechaCreacion;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
