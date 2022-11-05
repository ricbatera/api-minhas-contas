package com.minhascontas.domain.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PagarParcelaRequest {
	private Long idParcela;
	private String dataPagamento;
	private Long idConta;
	private BigDecimal valor;
}
