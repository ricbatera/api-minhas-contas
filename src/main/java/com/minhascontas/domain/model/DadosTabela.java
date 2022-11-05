package com.minhascontas.domain.model;

import java.time.OffsetDateTime;

import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DadosTabela {

	@JsonIgnore
	private String usuario = "Ricardo Alves Roberto";
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
