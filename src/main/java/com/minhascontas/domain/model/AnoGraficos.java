package com.minhascontas.domain.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnoGraficos {
	
	private int ano;
	private List<MesGraficos> meses;

}
