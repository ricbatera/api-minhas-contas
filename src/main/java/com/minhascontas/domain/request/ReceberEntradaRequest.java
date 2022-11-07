package com.minhascontas.domain.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ReceberEntradaRequest {
	private Long idParcela;
	private String dataRecebimento;
	private Long idConta;
	private BigDecimal valor;
}
