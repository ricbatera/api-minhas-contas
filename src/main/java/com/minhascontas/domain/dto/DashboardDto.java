package com.minhascontas.domain.dto;

import java.math.BigDecimal;
import java.util.List;

import com.minhascontas.domain.model.ContaBancaria;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DashboardDto {
	
	private List<CartaoCreditoDashboardDto> cartoes;
	private List<DebitoBoletoDashboardDto> debitoBoleto;
	private List<DevedortResponseDto> devedores;
	private List<ContaBancaria> contas;
	private BigDecimal totalSaidasDoMes;
	private BigDecimal totalEntradasDoMes;
	private BigDecimal totalPagoDoMes;
	private BigDecimal totalRecebidoDoMes;
	private BigDecimal totalEmBoletos;
	private BigDecimal minhasSaidas;
	private BigDecimal minhasEntradas;
	private SaldoDto saldo;
	private BigDecimal saldoAcumulado;
}
