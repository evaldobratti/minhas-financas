package org.bratti.service;

import org.bratti.repository.CategoriaRepository;
import org.bratti.repository.LocalRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {

	@Bean
	public ExtratoDadosProvider extratoDadosProvider(LocalRepository localRepository, CategoriaRepository categoriaRepository) {
		return new ExtratoDadosProviderImpl(localRepository, categoriaRepository);
	}
}
