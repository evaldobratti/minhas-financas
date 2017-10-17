package org.bratti.service.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import org.bratti.domain.Categoria;
import org.bratti.domain.Conta;
import org.bratti.domain.Lancamento;
import org.bratti.domain.Local;
import org.bratti.domain.Recorrencia;
import org.bratti.domain.enumeration.TipoFrequencia;

public class RecorrenciaDTO {

	private Long id;

	@NotNull
    private TipoFrequencia tipoFrequencia;

    @NotNull
    private Integer aCada;

    private BigDecimal valor;

    @NotNull
    private LocalDate partirDe;

    private Conta conta;

    private Categoria categoria;
    
    private Local local;
    
	private Lancamento lancamentoInicial;
	
	private LocalDate dataFim;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TipoFrequencia getTipoFrequencia() {
		return tipoFrequencia;
	}

	public void setTipoFrequencia(TipoFrequencia tipoFrequencia) {
		this.tipoFrequencia = tipoFrequencia;
	}

	public Integer getaCada() {
		return aCada;
	}

	public void setaCada(Integer aCada) {
		this.aCada = aCada;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public LocalDate getPartirDe() {
		return partirDe;
	}

	public void setPartirDe(LocalDate partirDe) {
		this.partirDe = partirDe;
	}

	public LocalDate getDataFim() {
		return dataFim;
	}

	public void setDataFim(LocalDate dataFim) {
		this.dataFim = dataFim;
	}

	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public Categoria getCategoria() {
		return categoria;
	}

	public void setCategoria(Categoria categoria) {
		this.categoria = categoria;
	}

	public Local getLocal() {
		return local;
	}

	public void setLocal(Local local) {
		this.local = local;
	}

	public Lancamento getLancamentoInicial() {
		return lancamentoInicial;
	}

	public void setLancamentoInicial(Lancamento lancamentoInicial) {
		this.lancamentoInicial = lancamentoInicial;
	}
	
	public RecorrenciaDTO() {
	}
	
	public RecorrenciaDTO(Recorrencia recorrencia) {
		this.setaCada(recorrencia.getaCada());
		this.setCategoria(recorrencia.getCategoria());
		this.setConta(recorrencia.getConta());
		this.setId(recorrencia.getId());
		this.setLocal(recorrencia.getLocal());
		this.setPartirDe(recorrencia.getPartirDe());
		this.setTipoFrequencia(recorrencia.getTipoFrequencia());
		this.setValor(recorrencia.getValor());
		this.setDataFim(recorrencia.getDataFim());
	}
	
	public Recorrencia toEntity() {
		Recorrencia recorrencia = new Recorrencia();
		recorrencia.setaCada(aCada);
		recorrencia.setCategoria(categoria);
		recorrencia.setConta(conta);
		recorrencia.setId(id);
		recorrencia.setLocal(local);
		recorrencia.setPartirDe(partirDe);
		recorrencia.setTipoFrequencia(tipoFrequencia);
		recorrencia.setValor(valor);
		recorrencia.setDataFim(dataFim);
		return recorrencia;
	}

}
