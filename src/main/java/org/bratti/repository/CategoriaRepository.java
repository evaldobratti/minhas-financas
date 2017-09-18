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

	@Query("select l.categoria from Lancamento l where l.local.nome = :nome")
	List<Categoria> getCategoriasDoLocal(@Param("nome") String nome);

	default Categoria getUltimaCategoriaDoLocal(String nome) {
		List<Categoria> categoriasDoLocal = getCategoriasDoLocal(nome);
		if (categoriasDoLocal.isEmpty())
			return null;
		return categoriasDoLocal.get(0);
	}
	
	
}