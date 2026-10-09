package co.edu.unicauca.servidor.capaAccesoADatos.repositories;

import co.edu.unicauca.servidor.capaAccesoADatos.models.TransaccionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransaccionRepository extends JpaRepository<TransaccionEntity, Long> {

    Optional<TransaccionEntity> findByTokenIdempotenciaToken(String token);

    List<TransaccionEntity> findByTokenIdempotenciaCedulaOrderByFechaAsc(String cedula);
}
