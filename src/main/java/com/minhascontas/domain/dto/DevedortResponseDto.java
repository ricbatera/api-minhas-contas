package com.minhascontas.domain.dto;

import java.math.BigDecimal;
import java.util.List;

import com.minhascontas.domain.model.Devedor;
import com.minhascontas.domain.model.ParcelaEntrada;

import lombok.Data;

@Data
public class DevedortResponseDto {
	
	private Devedor devedor;	
	private List<ParcelaEntrada> parcelas;
	private BigDecimal total;

}
