package com.minhascontas.domain.request;

import lombok.Data;

@Data
public class ContaBancariaRequest {
	private Long id;
	private String nome;
	private String obs;
	private Boolean status = true;
}
