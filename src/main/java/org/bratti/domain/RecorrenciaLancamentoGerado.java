package org.bratti.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class RecorrenciaLancamentoGerado extends LancamentoMotivo {
	
    private static final long serialVersionUID = 1L;
    
    @Column(name = "data")
    private LocalDate data;

    @OneToOne(mappedBy="motivo")
    private Lancamento lancamento;
    
    @ManyToOne
    private Recorrencia recorrencia;

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}

	public Lancamento getLancamento() {
		return lancamento;
	}

	public void setLancamento(Lancamento lancamento) {
		this.lancamento = lancamento;
	}

	public Recorrencia getRecorrencia() {
		return recorrencia;
	}

	public void setRecorrencia(Recorrencia recorrencia) {
		this.recorrencia = recorrencia;
	}
    
    

}
