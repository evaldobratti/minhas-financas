package org.bratti.domain;


import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A Lancamento.
 */
@Entity
@Table(name = "lancamento")
public class Lancamento extends UserOwned<Lancamento> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "data")
    private LocalDate data;

    @Column(name = "valor", precision=10, scale=2)
    private BigDecimal valor;

    @Column(name = "efetivada")
    private Boolean efetivada;

    @ManyToOne
    @NotNull
    private Conta conta;

    @ManyToOne
    @NotNull
    @Valid
    private Local local;

    @ManyToOne
    @NotNull
    private Categoria categoria;

    @OneToOne(cascade= {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinColumn(name="motivo_id")
    private LancamentoMotivo motivo;

    public Lancamento() {
        this.efetivada = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return data;
    }

    public Lancamento data(LocalDate data) {
        this.data = data;
        return this;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Lancamento valor(BigDecimal valor) {
        this.valor = valor;
        return this;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean isEfetivada() {
        return efetivada;
    }

    public Lancamento efetivada(Boolean efetivada) {
        this.efetivada = efetivada;
        return this;
    }

    public void setEfetivada(Boolean efetivada) {
        this.efetivada = efetivada;
    }

    public Conta getConta() {
        return conta;
    }

    public Lancamento conta(Conta conta) {
        this.conta = conta;
        return this;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public Local getLocal() {
        return local;
    }

    public Lancamento local(Local local) {
        this.local = local;
        return this;
    }

    public void setLocal(Local local) {
        this.local = local;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Lancamento categoria(Categoria categoria) {
        this.categoria = categoria;
        return this;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Lancamento lancamento = (Lancamento) o;
        if (lancamento.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), lancamento.getId());
    }

    public void setMotivo(LancamentoMotivo motivo) {
    	this.motivo = motivo;
    }

    public LancamentoMotivo getMotivo() {
    	return motivo;
    }

    public Lancamento motivo(LancamentoMotivo motivo) {
    	this.motivo = motivo;
    	return this;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Lancamento{" +
            "id=" + getId() +
            ", data='" + getData() + "'" +
            ", valor='" + getValor() + "'" +
            ", efetivada='" + isEfetivada() + "'" +
            "}";
    }

	public String getDescricao() {
		if (motivo == null)
			return this.local.getNome();
		return this.local.getNome() + " " + this.motivo.complementoDescricao();
	}
}
