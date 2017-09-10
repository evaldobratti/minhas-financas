package org.bratti.repository;

import org.bratti.domain.Conta;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import java.util.List;

/**
 * Spring Data JPA repository for the Conta entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ContaRepository extends UserOwnedRepository<Conta, Long> {

}
