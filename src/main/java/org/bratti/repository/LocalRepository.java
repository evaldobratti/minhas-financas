package org.bratti.repository;

import org.bratti.domain.Local;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Local entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LocalRepository extends JpaRepository<Local,Long> {
    
}
