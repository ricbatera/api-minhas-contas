package com.minhascontas.domain.model;

import java.math.BigDecimal;
import java.time.Month;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MesGraficos {
	private String nomeMes;
	private String nomeMesAbreviado;
	private int mesNumero;
	private BigDecimal valor;
	@JsonIgnore
	private Month mes;

}
