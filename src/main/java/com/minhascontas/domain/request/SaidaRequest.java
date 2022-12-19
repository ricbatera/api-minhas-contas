package com.minhascontas.domain.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SaidaRequest {
	private Long id;
	private String nome;
	private String obs;
	private String meioPagto;
	private String dataVencimento;
	private Integer qtdeParcelas;
	private Long cartaoSelecionado;
	private BigDecimal valor;
	private Boolean pago = false;
	private Long idContaBancaria;
	private Long idConta;
	private Long devedorId;
	private Long classificacaoId;
	private BigDecimal valorEntrada;
	private Boolean associaDevedor = false;
	private Boolean criaEntrada = false;
}
