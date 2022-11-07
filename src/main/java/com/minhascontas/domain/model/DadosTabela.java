package com.minhascontas.domain.model;

import java.time.OffsetDateTime;

import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DadosTabela {

	@JsonIgnore
	private String usuario = "API Minhas Contas";
	@JsonIgnore
	private OffsetDateTime dataCriacao;
	@JsonIgnore
	private OffsetDateTime dataAlteracao;

	@PrePersist
	private void setaDatas() {
		dataCriacao = OffsetDateTime.now();
		dataAlteracao = OffsetDateTime.now();
		System.out.println("Stenado data " + dataCriacao.toString());
	}

	@PreUpdate
	public void preUpdate() {
		dataAlteracao = OffsetDateTime.now();
	}

}
