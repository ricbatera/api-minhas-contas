package com.minhascontas.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.minhascontas.domain.model.ContaBancaria;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ParcelaDto {	
	private Long id;
	private LocalDate dataPagamento;
	private LocalDate dataVencimento;
	private BigDecimal valor;
	private BigDecimal valorPago;
	private ContaBancaria conta;
	private String situacao = "Aberto";
	private String contagemParcelas;
}
