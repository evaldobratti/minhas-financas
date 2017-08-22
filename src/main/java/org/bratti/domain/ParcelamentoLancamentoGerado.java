package org.bratti.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@DiscriminatorValue("Parcelamento")
public class ParcelamentoLancamentoGerado extends RecorrenciaLancamentoGerado {

	private int parcelaNumero;

	public ParcelamentoLancamentoGerado parcelaNumero(int parcelaNumero) {
		this.parcelaNumero = parcelaNumero;
		return this;
	}
	
	public int getParcelaNumero() {
		return parcelaNumero;
	}

	public void setParcelaNumero(int parcelaNumero) {
		this.parcelaNumero = parcelaNumero;
	}
	
	@Override
	public String complementoDescricao() {
		return "(" + parcelaNumero + "/" + getRecorrencia().getQuantidadeParcelas() + ")"; 
	}
	
	@Override
	public Parcelamento getRecorrencia() {
		return (Parcelamento) super.getRecorrencia();
	}
}
