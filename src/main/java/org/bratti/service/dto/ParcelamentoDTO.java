package org.bratti.service.dto;

import org.bratti.domain.Lancamento;

public class ParcelamentoDTO {

	private Integer inicioParcelas;
	private Integer quantidadeParcelas;
	private Lancamento lancamentoInicial;

	public Integer getInicioParcelas() {
		return inicioParcelas;
	}

	public void setInicioParcelas(Integer inicioParcelas) {
		this.inicioParcelas = inicioParcelas;
	}

	public Integer getQuantidadeParcelas() {
		return quantidadeParcelas;
	}

	public void setQuantidadeParcelas(Integer quantidadeParcelas) {
		this.quantidadeParcelas = quantidadeParcelas;
	}

	public Lancamento getLancamentoInicial() {
		return lancamentoInicial;
	}

	public void setLancamentoInicial(Lancamento lancamentoInicial) {
		this.lancamentoInicial = lancamentoInicial;
	}
}
