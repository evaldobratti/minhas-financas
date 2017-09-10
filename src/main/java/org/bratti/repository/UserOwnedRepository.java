package org.bratti.repository;

import java.io.Serializable;
import java.util.List;

import org.bratti.domain.UserOwned;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface UserOwnedRepository<T extends UserOwned<T>, ID extends Serializable> extends CrudRepository<T, ID> {
	<S extends T> S saveAndFlush(S entity);
	List<T> findAll();
}
