package org.bratti.repository.factory;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.bratti.domain.UserOwned;
import org.bratti.repository.UserOwnedRepository;
import org.bratti.repository.UserOwnedRepositoryImpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class RepoFactory<R extends JpaRepository<T, I>, T, I extends Serializable>
		extends JpaRepositoryFactoryBean<R, T, I> {

	public RepoFactory(Class<? extends R> repositoryInterface) {
		super(repositoryInterface);
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager em) {
		return new BaseRepositoryFactory(em);
	}

	public static class BaseRepositoryFactory<T, I extends Serializable> extends JpaRepositoryFactory {

		private final EntityManager em;

		public BaseRepositoryFactory(EntityManager em) {
			super(em);
			this.em = em;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected Object getTargetRepository(RepositoryInformation metadata) {
			System.out.println(metadata.getDomainType());
			
			if (UserOwnedRepository.class.isAssignableFrom(metadata.getRepositoryInterface()))
				return new UserOwnedRepositoryImpl(getEntityInformation(metadata.getDomainType()), em);
			else
				return super.getTargetRepository(metadata);
		}

		@Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
			if (UserOwnedRepository.class.isAssignableFrom(metadata.getRepositoryInterface()))
				return UserOwnedRepositoryImpl.class;
			else
				return super.getRepositoryBaseClass(metadata);
		}
	}
}
