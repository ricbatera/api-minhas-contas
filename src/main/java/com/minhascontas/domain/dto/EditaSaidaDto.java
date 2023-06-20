package com.minhascontas.domain.dto;

import java.util.List;

import com.minhascontas.domain.model.Classificacao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EditaSaidaDto {	
	private Long id;
	private String obs;
	private String nome;
	private List<Long> tags;
}
