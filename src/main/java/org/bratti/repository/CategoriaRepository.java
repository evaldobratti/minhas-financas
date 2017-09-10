package org.bratti.repository;

import java.util.List;

import org.bratti.domain.Categoria;
import org.bratti.domain.Conta;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Categoria entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CategoriaRepository extends UserOwnedRepository<Categoria, Long> {
}