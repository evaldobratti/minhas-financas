package org.bratti.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.SequenceGenerator;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@JsonDeserialize(as = RecorrenciaLancamentoGerado.class)
//@JsonDeserialize(using = LancamentoMotivoDeserializer.class)
public abstract class LancamentoMotivo {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;
    
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
