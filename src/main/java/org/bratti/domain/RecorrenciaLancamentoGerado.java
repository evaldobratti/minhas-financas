package org.bratti.domain;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Entity
@DiscriminatorValue("RecorrenciaLancamentoGerado")
public class RecorrenciaLancamentoGerado extends LancamentoMotivo {
	
    private static final long serialVersionUID = 1L;
    
    @Column(name = "data")
    private LocalDate data;

    @ManyToOne
    private Recorrencia recorrencia;

	public LocalDate getData() {
		return data;
	}

	public void setData(LocalDate data) {
		this.data = data;
	}
	
	public RecorrenciaLancamentoGerado data(LocalDate data) {
		this.data = data;
		return this;
	}

	public Recorrencia getRecorrencia() {
		return recorrencia;
	}

	public void setRecorrencia(Recorrencia recorrencia) {
		this.recorrencia = recorrencia;
	}
    
	public RecorrenciaLancamentoGerado recorrencia(Recorrencia recorrencia) {
		this.recorrencia = recorrencia;
		return this;
	}

}
