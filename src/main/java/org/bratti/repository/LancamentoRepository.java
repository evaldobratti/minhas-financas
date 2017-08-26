package org.bratti.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.bratti.domain.Conta;
import org.bratti.domain.Lancamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


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

    
    @Query("select sum(l.valor) from Lancamento l where l.conta = :conta and l.data <= :data")
	Optional<BigDecimal> findByContaAteDia(@Param("conta") Conta conta, @Param("data")LocalDate data);

    @Query("select l from Lancamento l where l.conta.id = :contaId and data >= :de and data <= :ate")
	List<Lancamento> findByConta(@Param("contaId") Long contaId, 
			@Param("de") LocalDate de, 
			@Param("ate") LocalDate ate);
}
