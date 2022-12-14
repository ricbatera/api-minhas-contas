package com.minhascontas.domain.request;

import lombok.Data;

@Data
public class CartaoCreditoRequest {
	private Long id;
	private String nome;
	private Integer diaVencimento;
	private String descricao;
	private Boolean status = true;
}
