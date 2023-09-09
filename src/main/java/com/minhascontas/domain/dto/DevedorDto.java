package com.minhascontas.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
public class DevedorDto {
	
	private Long id;
		
	private String nome;
	@JsonIgnore
	private String tipo;
	
	private Boolean status = true;

}
