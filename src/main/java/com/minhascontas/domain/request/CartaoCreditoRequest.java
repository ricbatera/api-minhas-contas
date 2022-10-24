package com.minhascontas.domain.request;

import javax.persistence.Embedded;

import com.minhascontas.domain.model.DadosTabela;

import lombok.Data;

@Data
public class CartaoCreditoRequest {
	private String nome;
	private Integer diaVencimento;
	private String descricao;
}
