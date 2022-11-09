package com.minhascontas.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CartaoCreditoDashboardDto {
	
	private Long id;
	private String nome;
	private String descricao;
	private BigDecimal valorFatura;
	private Long idFatura;
	private LocalDate vencimento;

}
