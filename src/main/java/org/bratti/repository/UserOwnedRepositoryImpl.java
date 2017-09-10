package org.bratti.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.NotImplementedException;
import org.bratti.domain.User;
import org.bratti.domain.UserOwned;
import org.bratti.security.SecurityUtils;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.transaction.annotation.Transactional;

public class UserOwnedRepositoryImpl<T extends UserOwned<T>, ID extends Serializable> implements UserOwnedRepository<T, ID> {

	private EntityManager em;
	private EntityInformation<T, ID> entityInformation;
	
	public UserOwnedRepositoryImpl(EntityInformation<T, ID>  entityInformation, EntityManager em) {
		this.entityInformation = entityInformation;
		this.em = em;
	}
	
	@Override
	@Transactional
	public <S extends T> S save(S entity) {
		entity.setUsuario(getLoggedUser());
		
		if (entityInformation.isNew(entity)) {
			em.persist(entity);
			return entity;
		} else {
			return em.merge(entity);
		}
	}

	private User getLoggedUser() {
		Query query = em.createQuery("from User where login = :login");
		query.setParameter("login", SecurityUtils.getCurrentUserLogin());
		User singleResult = (User) query.getSingleResult();
		return singleResult;
	}
	
	@Override
	public List<T> findAll() {
		TypedQuery<T> query = em.createQuery("from " + entityInformation.getJavaType().getName() + " e where e.usuario = :usuario", entityInformation.getJavaType());
		query.setParameter("usuario", getLoggedUser());
		return query.getResultList();
	}

	public <S extends T> S saveAndFlush(S entity) {
		S result = save(entity);
		em.flush();

		return result;
	}
	@Override
	public long count() {
		TypedQuery<Long> query = em.createQuery("select count(1) from " + entityInformation.getJavaType().getName() + " e where e.usuario = :usuario", Long.class);
		query.setParameter("usuario", getLoggedUser());
		return query.getSingleResult();
	}

	@Override
	public void delete(ID id) {
		T entity = findOne(id);
		delete(entity);
	}

	@Override
	public void delete(T entity) {
		em.remove(em.contains(entity) ? entity : em.merge(entity));		
	}

	@Override
	public void delete(Iterable<? extends T> entities) {
		for (T entity : entities) {
			delete(entity);
		}
	}

	@Override
	public void deleteAll() {
		for (T element : findAll()) {
			delete(element);
		}
	}

	@Override
	public boolean exists(ID id) {
		throw new NotImplementedException("exists(id) não implementado");
	}

	@Override
	public Iterable<T> findAll(Iterable<ID> ids) {
		throw new NotImplementedException("findAll(ids) não implementado");
	}

	@Override
	public T findOne(ID id) {
		return em.find(entityInformation.getJavaType(), id);
	}

	@Override
	public <S extends T> Iterable<S> save(Iterable<S> entities) {
		List<S> result = new ArrayList<S>();
		
		for (S entity : entities) {
			result.add(save(entity));
		}

		return result;
	}

}
