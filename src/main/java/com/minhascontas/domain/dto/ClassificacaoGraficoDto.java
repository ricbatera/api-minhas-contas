package com.minhascontas.domain.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClassificacaoGraficoDto {
	
	private String nome;
	private BigDecimal valor;

}
