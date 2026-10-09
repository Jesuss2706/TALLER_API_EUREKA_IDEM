package co.edu.unicauca.servidor.capaAccesoADatos.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Historial de transacciones. La columna del token es UNIQUE: un token de idempotencia
 * solo puede quedar asociado a una transaccion, aunque lleguen peticiones concurrentes.
 */
@Entity
@Table(name = "historial_transacciones")
public class TransaccionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "token", nullable = false, unique = true)
    private TokenIdempotenciaEntity tokenIdempotencia;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false)
    private LocalDateTime fecha;

    public TransaccionEntity() {
    }

    public TransaccionEntity(TokenIdempotenciaEntity tokenIdempotencia, BigDecimal monto, LocalDateTime fecha) {
        this.tokenIdempotencia = tokenIdempotencia;
        this.monto = monto;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TokenIdempotenciaEntity getTokenIdempotencia() {
        return tokenIdempotencia;
    }

    public void setTokenIdempotencia(TokenIdempotenciaEntity tokenIdempotencia) {
        this.tokenIdempotencia = tokenIdempotencia;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
