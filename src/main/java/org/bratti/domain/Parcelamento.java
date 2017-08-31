package org.bratti.domain;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import org.bratti.domain.enumeration.TipoFrequencia;

@Entity
@DiscriminatorValue("Parcelamento")
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

	public Parcelamento iniciandoNaParcela(int inicioParcelas) {
		this.inicioParcelas = inicioParcelas;
		return this;
	}

	public void efetivaCom(Lancamento lancamento) {
		if (lancamento != null) {
			RecorrenciaLancamentoGerado recorrencia = (RecorrenciaLancamentoGerado) new ParcelamentoLancamentoGerado()
					.parcelaNumero(inicioParcelas)
					.data(getPartirDe())
					.recorrencia(this);
			addRecorrenciaLancamentos(recorrencia);
			lancamento.setMotivo(recorrencia);
		}
	}

	public static Parcelamento novoMensal() {
		return (Parcelamento) new Parcelamento().aCada(1).tipoFrequencia(TipoFrequencia.MES);
	}

}
