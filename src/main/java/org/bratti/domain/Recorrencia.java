package org.bratti.domain;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.bratti.domain.enumeration.TipoFrequencia;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A Recorrencia.
 */
@Entity
@Table(name = "recorrencia")
public class Recorrencia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_frequencia", nullable = false)
    private TipoFrequencia tipoFrequencia;

    @NotNull
    @Min(value = 1)
    @Column(name = "a_cada", nullable = false)
    private Integer aCada;

    @Column(name = "valor", precision=10, scale=2)
    private BigDecimal valor;

    @NotNull
    @Min(value = 1)
    @Max(value = 31)
    @Column(name = "dia", nullable = false)
    private Integer dia;

    @NotNull
    @Column(name = "partir_de", nullable = false)
    private LocalDate partirDe;

    @ManyToMany
    @ManyToOne(optional = false)
    @NotNull
    private Conta conta;

    @ManyToOne
    private Categoria categoria;
    
    @ManyToOne
    private Local local;
    
    @OneToMany(mappedBy="recorrencia")
    @JsonIgnore
    private Set<RecorrenciaLancamentoGerado> recorrenciaLancamentos = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoFrequencia getTipoFrequencia() {
        return tipoFrequencia;
    }

    public Recorrencia tipoFrequencia(TipoFrequencia tipoFrequencia) {
        this.tipoFrequencia = tipoFrequencia;
        return this;
    }

    public void setTipoFrequencia(TipoFrequencia tipoFrequencia) {
        this.tipoFrequencia = tipoFrequencia;
    }

    public Integer getaCada() {
        return aCada;
    }

    public Recorrencia aCada(Integer aCada) {
        this.aCada = aCada;
        return this;
    }

    public void setaCada(Integer aCada) {
        this.aCada = aCada;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Recorrencia valor(BigDecimal valor) {
        this.valor = valor;
        return this;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getDia() {
        return dia;
    }

    public Recorrencia dia(Integer dia) {
        this.dia = dia;
        return this;
    }

    public void setDia(Integer dia) {
        this.dia = dia;
    }

    public LocalDate getPartirDe() {
        return partirDe;
    }

    public Recorrencia partirDe(LocalDate partirDe) {
        this.partirDe = partirDe;
        return this;
    }

    public void setPartirDe(LocalDate partirDe) {
        this.partirDe = partirDe;
    }

    public Set<RecorrenciaLancamentoGerado> getRecorrenciaLancamentos() {
        return recorrenciaLancamentos;
    }

    public Recorrencia recorrenciaLancamentos(Set<RecorrenciaLancamentoGerado> recorrenciaLancamentos) {
        this.recorrenciaLancamentos = recorrenciaLancamentos;
        return this;
    }

    public Recorrencia addRecorrenciaLancamentos(RecorrenciaLancamentoGerado recorrenciaLancamento) {
        this.recorrenciaLancamentos.add(recorrenciaLancamento);
        return this;
    }

    public Recorrencia removeRecorrenciaLancamentos(RecorrenciaLancamentoGerado recorrenciaLancamento) {
        this.recorrenciaLancamentos.remove(recorrenciaLancamento);
        return this;
    }

    public void setRecorrenciaLancamentos(Set<RecorrenciaLancamentoGerado> recorrenciaLancamentos) {
        this.recorrenciaLancamentos = recorrenciaLancamentos;
    }

    public Conta getConta() {
        return conta;
    }

    public Recorrencia conta(Conta conta) {
        this.conta = conta;
        return this;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public Recorrencia categoria(Categoria categoria) {
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
        Recorrencia recorrencia = (Recorrencia) o;
        if (recorrencia.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), recorrencia.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Recorrencia{" +
            "id=" + getId() +
            ", tipoFrequencia='" + getTipoFrequencia() + "'" +
            ", aCada='" + getaCada() + "'" +
            ", valor='" + getValor() + "'" +
            ", dia='" + getDia() + "'" +
            ", partirDe='" + getPartirDe() + "'" +
            "}";
    }
    
	public List<Lancamento> projecaoAte(LocalDate ate) {
		if (ate.isBefore(partirDe))
			return new ArrayList<>();
		
		LocalDate dataBase = LocalDate.of(partirDe.getYear(), partirDe.getMonth(), dia);
		int cada = aCada;
		List<Lancamento> retorno = new ArrayList<>();
		
		if (dataBase.isAfter(partirDe)) {
			retorno.add(new Lancamento()
						.categoria(categoria)
						.conta(conta)
						.data(dataBase)
						.local(local)
						.valor(valor)
						.motivo(new RecorrenciaLancamentoGerado()
								.data(dataBase)
								.recorrencia(this)));
		}
		
		while (dataBase.isBefore(ate) || dataBase.equals(ate)) {
			cada -= 1;
			dataBase = dataBase.plus(1, tipoFrequencia.unit());
			
			if (cada == 0) {
				cada = aCada;
				LocalDate data = LocalDate.of(dataBase.getYear(), dataBase.getMonth(), dataBase.getDayOfMonth());
				retorno.add(new Lancamento()
						.categoria(categoria)
						.conta(conta)
						.data(data)
						.local(local)
						.valor(valor)
						.motivo(new RecorrenciaLancamentoGerado()
								.data(data)
								.recorrencia(this)));
			}
			
		}
		List<LocalDate> datasGeradas = recorrenciaLancamentos.stream().map(i -> i.getData()).collect(Collectors.toList());
		
		return retorno.stream().filter(l -> 
			(l.getData().isBefore(ate) || l.getData().equals(ate))
			&& !datasGeradas.contains(l.getData())).collect(Collectors.toList());
	}

	public Local getLocal() {
		return local;
	}

	public void setLocal(Local local) {
		this.local = local;
	}
}
