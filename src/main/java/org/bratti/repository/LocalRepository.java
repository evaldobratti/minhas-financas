package org.bratti.repository;

import org.bratti.domain.Local;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * Spring Data JPA repository for the Local entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LocalRepository extends UserOwnedRepository<Local,Long> {
    
	@Query("select l from Local l where l.usuario.login = ?#{principal.username} and lower(l.nome) = lower(:nome)")
    public Local findOneByNomeIgnoreCase(@Param("nome") String nome);
}
