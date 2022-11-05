package com.minhascontas.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CartaoCreditoDto {
	
	private Long id;
	private String nome;
	private String descricao;
	private Integer vencimentoDia;
	private Boolean status;

}
