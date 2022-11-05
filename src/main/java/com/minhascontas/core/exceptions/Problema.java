package com.minhascontas.core.exceptions;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Problema {
	
	private String mensagem;
	private OffsetDateTime data;
	private String mensagemSistema;

}
