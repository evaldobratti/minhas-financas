package org.bratti.domain;


import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A Categoria.
 */
@Entity
@Table(name = "categoria")
public class Categoria extends UserOwned<Categoria> {

    private static final long serialVersionUID = 1L;

    @Column(name = "nome")
    @Size(min=1, max=255)
    @NotNull
    private String nome;

    @ManyToOne
    private Categoria pai;


    public Categoria() { }

    public Categoria(Long id) {
        this.setId(id);
    }

    public String getNome() {
        return nome;
    }

    public Categoria nome(String nome) {
        this.nome = nome;
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Categoria getPai() {
        return pai;
    }

    public Categoria pai(Categoria categoria) {
        this.pai = categoria;
        return this;
    }

    public void setPai(Categoria categoria) {
        this.pai = categoria;
    }

    // public void setFilhas(List<Categoria> categorias) {
    //     this.filhas = categorias;
    // }

    // public List<Categoria> getFilhas() {
    //     return this.filhas;
    // }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Categoria categoria = (Categoria) o;
        if (categoria.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), categoria.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Categoria{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            "}";
    }
}
