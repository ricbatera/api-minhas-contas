package com.minhascontas.domain.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.Data;

@Data
public class GraficoCategoriasPeriodoDto {
	private Set<String> categorias = new HashSet<>();
	private List<BigDecimal> valores = new ArrayList<>();
}
