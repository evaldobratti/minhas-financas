package org.bratti.repository;

import org.bratti.domain.Recorrencia;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

/**
 * Spring Data JPA repository for the Recorrencia entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RecorrenciaRepository extends JpaRepository<Recorrencia,Long> {
    
    @Query("select distinct recorrencia from Recorrencia recorrencia left join fetch recorrencia.lancamentos")
    List<Recorrencia> findAllWithEagerRelationships();

    @Query("select recorrencia from Recorrencia recorrencia left join fetch recorrencia.lancamentos where recorrencia.id =:id")
    Recorrencia findOneWithEagerRelationships(@Param("id") Long id);
    
}
