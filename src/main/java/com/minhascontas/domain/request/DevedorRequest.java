package com.minhascontas.domain.request;

import javax.persistence.Embedded;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.minhascontas.domain.model.DadosTabela;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DevedorRequest {
	
	private Long id;
		
	private String nome;
	
	private Boolean status = true;
	
	@JsonIgnore
	@Embedded
	private DadosTabela dados =new DadosTabela();

}
