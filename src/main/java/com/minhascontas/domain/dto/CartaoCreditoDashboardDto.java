package com.minhascontas.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.minhascontas.domain.model.CartaoCredito;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CartaoCreditoDashboardDto {
	
	private Long id;	
	private BigDecimal valor;
	private LocalDate vencimento;
	private CartaoCredito cartao;
	private Boolean situacao;

}
