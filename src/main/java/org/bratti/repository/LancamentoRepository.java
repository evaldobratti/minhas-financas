package org.bratti.repository;

import java.util.List;


import org.bratti.domain.Lancamento;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;


/**
 * Spring Data JPA repository for the Lancamento entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LancamentoRepository extends JpaRepository<Lancamento,Long> {
    
    @Query("select l from Lancamento l where l.conta.id = :contaId and month(l.data) = :mes and year(l.data) = :ano")
    List<Lancamento> findByContaMesAno(@Param("contaId") Long contaId,
        @Param("mes") int mes,
        @Param("ano") int ano);
}
