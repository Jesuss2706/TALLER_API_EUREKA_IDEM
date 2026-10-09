package co.edu.unicauca.servidor.capaAccesoADatos.repositories;

import co.edu.unicauca.servidor.capaAccesoADatos.models.TokenIdempotenciaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenIdempotenciaRepository extends JpaRepository<TokenIdempotenciaEntity, String> {
}
