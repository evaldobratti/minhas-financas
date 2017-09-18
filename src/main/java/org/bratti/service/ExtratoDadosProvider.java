package org.bratti.service;

import org.bratti.domain.Categoria;
import org.bratti.domain.Local;

public interface ExtratoDadosProvider {

	public Local getLocal(String string);

	public Categoria getUltimaCategoriaDoLocal(String nome);

}
