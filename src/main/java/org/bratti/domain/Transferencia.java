package org.bratti.domain;


import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A Transferencia.
 */
@Entity
@Table(name = "transferencia")
public class Transferencia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "valor", precision=10, scale=2)
    private BigDecimal valor;

    @OneToOne
    @JoinColumn(unique = true)
    private Conta contaOrigem;

    @OneToOne
    @JoinColumn(unique = true)
    private Conta contaDestino;

    @OneToOne
    @JoinColumn(unique = true)
    private Lancamento lancamentoOrigem;

    @OneToOne
    @JoinColumn(unique = true)
    private Lancamento lancamentoDestino;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Transferencia valor(BigDecimal valor) {
        this.valor = valor;
        return this;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Conta getContaOrigem() {
        return contaOrigem;
    }

    public Transferencia contaOrigem(Conta conta) {
        this.contaOrigem = conta;
        return this;
    }

    public void setContaOrigem(Conta conta) {
        this.contaOrigem = conta;
    }

    public Conta getContaDestino() {
        return contaDestino;
    }

    public Transferencia contaDestino(Conta conta) {
        this.contaDestino = conta;
        return this;
    }

    public void setContaDestino(Conta conta) {
        this.contaDestino = conta;
    }

    public Lancamento getLancamentoOrigem() {
        return lancamentoOrigem;
    }

    public Transferencia lancamentoOrigem(Lancamento lancamento) {
        this.lancamentoOrigem = lancamento;
        return this;
    }

    public void setLancamentoOrigem(Lancamento lancamento) {
        this.lancamentoOrigem = lancamento;
    }

    public Lancamento getLancamentoDestino() {
        return lancamentoDestino;
    }

    public Transferencia lancamentoDestino(Lancamento lancamento) {
        this.lancamentoDestino = lancamento;
        return this;
    }

    public void setLancamentoDestino(Lancamento lancamento) {
        this.lancamentoDestino = lancamento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Transferencia transferencia = (Transferencia) o;
        if (transferencia.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), transferencia.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Transferencia{" +
            "id=" + getId() +
            ", valor='" + getValor() + "'" +
            "}";
    }
}
