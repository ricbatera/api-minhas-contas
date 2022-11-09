package com.minhascontas.domain.dto;

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
public class ClassificacaoDto {
	
	private Long id;
		
	private String nome;
	
	private String tipo;
	
	private Boolean status = true;

}
