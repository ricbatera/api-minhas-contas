package com.minhascontas.domain.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaldoDto {
	
	private BigDecimal totalSaidas;
	private BigDecimal totalEntradas;
	private BigDecimal saldo;
}
