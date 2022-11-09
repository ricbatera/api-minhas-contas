package com.minhascontas.domain.request;

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
public class ClassificacaoRequest {
	
	private Long id;
		
	private String nome;
	
	private String tipo;
	
	private Boolean status = true;

}
