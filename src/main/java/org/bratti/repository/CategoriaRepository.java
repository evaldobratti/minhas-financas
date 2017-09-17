package org.bratti.repository;

import java.util.List;

import org.bratti.domain.Categoria;
import org.bratti.domain.Conta;
import org.bratti.domain.Local;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;


/**
 * Spring Data JPA repository for the Categoria entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoriaRepository extends UserOwnedRepository<Categoria, Long> {

	@Query("select l.categoria from Lancamento l where l.local = :local")
	List<Categoria> getCategoriasDoLocal(@Param("local") Local local);

	default Categoria getUltimaCategoriaDoLocal(Local local) {
		return getCategoriasDoLocal(local).get(0);
	}
	
	
}