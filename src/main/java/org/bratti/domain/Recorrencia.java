package org.bratti.domain;


import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.bratti.domain.enumeration.TipoFrequencia;

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
    @JoinTable(name = "recorrencia_lancamento",
               joinColumns = @JoinColumn(name="recorrencias_id", referencedColumnName="id"),
               inverseJoinColumns = @JoinColumn(name="lancamentos_id", referencedColumnName="id"))
    private Set<Lancamento> lancamentos = new HashSet<>();

    @ManyToOne(optional = false)
    @NotNull
    private Conta conta;

    @ManyToOne
    private Categoria categoria;

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

    public Set<Lancamento> getLancamentos() {
        return lancamentos;
    }

    public Recorrencia lancamentos(Set<Lancamento> lancamentos) {
        this.lancamentos = lancamentos;
        return this;
    }

    public Recorrencia addLancamento(Lancamento lancamento) {
        this.lancamentos.add(lancamento);
        return this;
    }

    public Recorrencia removeLancamento(Lancamento lancamento) {
        this.lancamentos.remove(lancamento);
        return this;
    }

    public void setLancamentos(Set<Lancamento> lancamentos) {
        this.lancamentos = lancamentos;
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
		if (ate.isBefore(partirDe) || partirDe.equals(ate) )
			return Collections.EMPTY_LIST;
		
		LocalDate dataBase = LocalDate.of(partirDe.getYear(), partirDe.getMonth(), dia);
		LocalDate dataNormalizada = dataBase;
		
		LocalDate ateNormalizada = LocalDate.of(ate.getYear(), ate.getMonth(), 1)
				.plus(1, ChronoUnit.MONTHS);
			
		List<Lancamento> retorno = new ArrayList<>();
		if (tipoFrequencia == TipoFrequencia.DIA) {
			while (dataBase.isBefore(partirDe)) {
				dataBase = dataBase.plus(1, ChronoUnit.DAYS);
			}
			
			long dias = ChronoUnit.DAYS.between(partirDe, ateNormalizada);
			int cadaCount = aCada;
			
			if (dataNormalizada.isAfter(partirDe))
				cadaCount = 1;
			else if (dataNormalizada.equals(partirDe))
				cadaCount++;
			
			for (int i = 0; i <= dias; i++) {
				cadaCount -= 1;
				if (cadaCount == 0) {
					cadaCount = aCada;
					retorno.add(new Lancamento()
							.conta(conta)
							.categoria(categoria)
							.valor(valor)
							.data(dataBase.plus(i, ChronoUnit.DAYS)));
				}
			}
		}
		
		if (tipoFrequencia == TipoFrequencia.MES) {
			
			long meses = ChronoUnit.MONTHS.between(dataNormalizada, ateNormalizada);
			int cadaCount = aCada;
			
			if (dataNormalizada.isAfter(partirDe))
				cadaCount = 1;
			
			if (dataNormalizada.equals(partirDe))
				cadaCount++;
			
			for (int i = 0; i <= meses; i++) {
				cadaCount -= 1;
				if (cadaCount == 0) {
					cadaCount = aCada;
					retorno.add(new Lancamento()
							.conta(conta)
							.categoria(categoria)
							.valor(valor)
							.data(dataBase.plus(i, ChronoUnit.MONTHS)));
				}
			}
		}

		return retorno.stream()
				.filter(l -> l.getData().isAfter(partirDe) 
						&& (l.getData().isBefore(ate) || l.getData().equals(ate)))
				.collect(Collectors.toList());
	}
}
