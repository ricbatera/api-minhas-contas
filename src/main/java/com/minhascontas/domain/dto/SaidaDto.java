package com.minhascontas.domain.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.minhascontas.domain.model.CartaoCredito;
import com.minhascontas.domain.model.Classificacao;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SaidaDto {	
	private Long id;
	private String obs;
	private String nome;
	private String meioPagto;
	private List<ParcelaDto> parcelas = new ArrayList<>();
	private BigDecimal total;
	private int diaVencimento;
	private String xDeParcelas;
	private CartaoCredito cartao;
	private List<Classificacao> tags = new ArrayList<>();
}
