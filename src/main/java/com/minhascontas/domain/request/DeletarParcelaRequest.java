package com.minhascontas.domain.request;

import lombok.Data;

@Data
public class DeletarParcelaRequest {
	
	private Long idParcela;
	private Boolean deletarTudo;
	private Boolean deletarRestante;
	
	

}
