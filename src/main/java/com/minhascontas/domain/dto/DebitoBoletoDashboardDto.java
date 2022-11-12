package com.minhascontas.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.minhascontas.domain.model.Saida;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DebitoBoletoDashboardDto {
	
	private Long id;
	private BigDecimal valor;
	private LocalDate vencimento;
	private Saida saida;
	private String situacao;

}
