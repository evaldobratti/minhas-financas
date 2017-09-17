package org.bratti.service;

import org.bratti.domain.Categoria;
import org.bratti.domain.Local;
import org.bratti.repository.CategoriaRepository;
import org.bratti.repository.LocalRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class ExtratoDadosProviderImpl implements ExtratoDadosProvider {

	private LocalRepository localRepository;
	
	private CategoriaRepository categoriaRepository;

	public ExtratoDadosProviderImpl(LocalRepository localRepository, CategoriaRepository categoriaRepository) {
		this.localRepository = localRepository;
		this.categoriaRepository = categoriaRepository;
	}

	public Local getLocal(String string) {
		return localRepository.findOneByNomeIgnoreCase(string);
	}

	public Categoria getUltimaCategoriaDoLocal(Local local) {
		return categoriaRepository.getUltimaCategoriaDoLocal(local);
	}

}
