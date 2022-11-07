package com.minhascontas.domain.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class EntradaRequest {
	private Long id;
	private String nome;
	private String obs;
	private String dataPrevistaRecebimento;
	private Integer qtdeParcelas;
	private BigDecimal valor;
	private Boolean recebido = false;
	private Long idConta;
}
