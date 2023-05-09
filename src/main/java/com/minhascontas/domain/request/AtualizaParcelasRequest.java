package com.minhascontas.domain.request;

import java.util.List;

import com.minhascontas.domain.model.Parcela;

import lombok.Data;

@Data
public class AtualizaParcelasRequest {
	
	private Long idSaida;
	private List<Parcela> parcelas;

}
