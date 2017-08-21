package org.bratti.domain;

import java.time.LocalDate;
import java.util.List;

public class Parcelamento extends Recorrencia {

	private static final long serialVersionUID = -6332880505277733471L;


	int quantidadeParcelas;


	private int inicioParcelas = 1;
	
	public void setQuantidadeParcelas(int quantidadeParcelas) {
		this.quantidadeParcelas = quantidadeParcelas;
	}
	
	public int getQuantidadeParcelas() {
		return quantidadeParcelas;
	}
	
	public Parcelamento quantidadeParcelas(int quantidadeParcelas) {
		this.quantidadeParcelas = quantidadeParcelas;
		return this;
	}

	@Override
	protected boolean deveParar(boolean gerouParcelaInicial, List<Lancamento> retorno) {
		int parcelasReais = quantidadeParcelas - inicioParcelas + 1 ;
		return gerouParcelaInicial ? retorno.size() == parcelasReais : retorno.size() == parcelasReais - 1;
	}
	
	@Override
	public RecorrenciaLancamentoGerado novoMotivo(LocalDate dataBase, boolean gerouParcelaInicial, int ix) {
		ix += inicioParcelas - 1;
		
		return new ParcelamentoLancamentoGerado()
				.parcelaNumero(ix)
				.data(dataBase)
				.recorrencia(this);
	}

	public Recorrencia iniciandoNaParcela(int inicioParcelas) {
		this.inicioParcelas = inicioParcelas;
		return this;
	}
	
	public void efetivaCom(Lancamento lancamento) {
		addRecorrenciaLancamentos((RecorrenciaLancamentoGerado) new ParcelamentoLancamentoGerado()
				.parcelaNumero(1)
				.data(getPartirDe())
				.recorrencia(this));
	}

}
