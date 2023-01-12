package com.minhascontas.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.minhascontas.domain.model.Classificacao;
import com.minhascontas.domain.model.ContaBancaria;
import com.minhascontas.domain.model.Devedor;
import com.minhascontas.domain.model.Entrada;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemListaEntradaDto {
	
	private Long id;
	private LocalDate dataPrevistaRecebimento;
	private LocalDate dataRecebimento;
	private BigDecimal valor;
	private BigDecimal valorRecebido;
	private Entrada entrada;
	private String situacao;
	private ContaBancaria conta;
	private Devedor devedor;
	private Classificacao classificacao;

}
