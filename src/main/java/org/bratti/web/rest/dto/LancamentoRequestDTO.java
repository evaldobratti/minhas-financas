package org.bratti.web.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.bratti.domain.Categoria;
import org.bratti.domain.Conta;

public class LancamentoRequestDTO {

    private Long id;
    private LocalDate data;
    private BigDecimal valor;
    private Conta conta;
    private String local;
    private Categoria categoria;
    private Boolean efetivada;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public LocalDate getData() {
        return this.data;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public Conta getConta() {
        return conta;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getLocal() {
        return local;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setEfetivada(Boolean efetivada) {
        this.efetivada = efetivada;
    }

	public Boolean getEfetivada() {
		return efetivada;
	}

}