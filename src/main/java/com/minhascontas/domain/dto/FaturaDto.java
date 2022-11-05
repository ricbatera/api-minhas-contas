package com.minhascontas.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.minhascontas.domain.model.ContaBancaria;
import com.minhascontas.domain.model.Parcela;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FaturaDto {
	
	private Long id;
	private LocalDate dataVencimento;
	private LocalDate dataPagamento;
	private BigDecimal valor;
	private BigDecimal valorPago;
	private Boolean status;
	private Boolean situacao= true;
	private ContaBancaria conta;
	private List<Parcela> itensFatura = new ArrayList<>();
}
