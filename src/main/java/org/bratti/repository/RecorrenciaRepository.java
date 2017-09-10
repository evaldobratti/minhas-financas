package org.bratti.repository;

import org.bratti.domain.Recorrencia;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Spring Data JPA repository for the Recorrencia entity.
 */
@Repository
public interface RecorrenciaRepository extends UserOwnedRepository<Recorrencia,Long> {
    
    @Query("select distinct recorrencia from Recorrencia recorrencia left join fetch recorrencia.recorrenciaLancamentos where recorrencia.usuario.login = ?#{principal.username}")
    List<Recorrencia> findAllWithEagerRelationships();

    @Query("select recorrencia from Recorrencia recorrencia left join fetch recorrencia.recorrenciaLancamentos where recorrencia.id =:id and recorrencia.usuario.login = ?#{principal.username}")
    Recorrencia findOneWithEagerRelationships(@Param("id") Long id);
    
}
