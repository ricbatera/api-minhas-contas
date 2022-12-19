package com.minhascontas.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.minhascontas.domain.model.ContaBancaria;
import com.minhascontas.domain.model.Fatura;
import com.minhascontas.domain.model.Saida;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemListaSaidaDto {
	
	private Long id;
	private LocalDate dataVencimento;
	private LocalDate dataPagamento;
	private BigDecimal valor;
	private BigDecimal valorPago;
	private Boolean status;
	private Fatura fatura;
	private Saida saida;
	private String situacao;
	private ContaBancaria conta;
	private String contagemParcelas;

}
