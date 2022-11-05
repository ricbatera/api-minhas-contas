package com.minhascontas.domain.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ContaBancariaDto {
	
	private Long id;
	private String nome;
	private String obs;
	private BigDecimal saldo;
	private Boolean status;

}
