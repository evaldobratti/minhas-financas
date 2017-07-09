package org.bratti.repository;

import org.bratti.domain.Transferencia;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Transferencia entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TransferenciaRepository extends JpaRepository<Transferencia,Long> {
    
}
