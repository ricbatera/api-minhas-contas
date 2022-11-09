package com.minhascontas.domain.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DashboardDto {
	
	private List<CartaoCreditoDashboardDto> cartoes;
	private List<DebitoBoletoDashboardDto> debitoBoleto;

}
