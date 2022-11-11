package com.minhascontas.domain.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PagarFaturaRequest {
	private Long idFatura;
	private String dataPagamento;
	private Long idConta;
	private BigDecimal valor;
	private Boolean gerarParcelaComDiferenca = false;
	private Long classificacaoId;
	private Boolean associaDevedor = false;
	private Long devedorId;
}
