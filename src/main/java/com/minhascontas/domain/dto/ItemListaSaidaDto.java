package com.minhascontas.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.minhascontas.domain.model.ContaBancaria;
import com.minhascontas.domain.model.Fatura;
import com.minhascontas.domain.model.Saida;
import com.minhascontas.domain.model.Tag;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemListaSaidaDto {
	
	private Long id;
	private LocalDate dataVencimento;
	private LocalDate dataPagamento;
	private LocalDate saidaDataCompra;
	private BigDecimal valor;
	private BigDecimal valorPago;
	private Boolean status;
	private Fatura fatura;
	private Saida saida;
	private String situacao;
	private ContaBancaria conta;
	private String contagemParcelas;
	private String devedorNome;
	private String classificacaoNome;
	private List<Tag> tags;

}
