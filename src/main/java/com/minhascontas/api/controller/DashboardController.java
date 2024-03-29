package com.minhascontas.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minhascontas.domain.dto.ClassificacaoGraficoDto;
import com.minhascontas.domain.dto.DashboardDto;
import com.minhascontas.domain.dto.GraficoCategoriasPeriodoDto;
import com.minhascontas.domain.model.AnoGraficos;
import com.minhascontas.domain.service.DashboardService;

@CrossOrigin
@RestController
@RequestMapping("/dashboard")
public class DashboardController {
	
	@Autowired
	private DashboardService service;
		
	@GetMapping("/indicadores")
	public DashboardDto indicadores(@Param(value = "mes") int mes, @Param(value = "ano") int ano) {
		return service.getIndicadores(mes, ano);
	}
	
	@GetMapping("/graficos")
	private GraficoCategoriasPeriodoDto getGraficos(@Param(value = "mesIn") int mesIn, @Param(value = "anoIn") int anoIn, @Param(value = "mesOut") int mesOut, @Param(value = "anoOut") int anoOut) {
		return service.getGraficos(mesIn, anoIn, mesOut, anoOut);
	}
	
	@GetMapping("/graf-mes-geral")
	private List<ClassificacaoGraficoDto> graficoMesGeral(
			@Param(value = "mesStart") int mesStart, 
			@Param(value = "anoStart") int anoStart, 
			@Param(value = "mesEnd") int mesEnd, 
			@Param(value = "anoEnd") int anoEnd) {
		return service.graficoGeralMes(mesStart, anoStart, mesEnd, anoEnd);
	}
	
	@GetMapping("/graf-geral")
	private List<AnoGraficos> graficoGeral(
			@Param(value = "mesStart") int mesStart, 
			@Param(value = "anoStart") int anoStart, 
			@Param(value = "mesEnd") int mesEnd, 
			@Param(value = "anoEnd") int anoEnd,
			@Param(value = "idDevedor") Long idDevedor) {
		return service.graficoGeral(mesStart, anoStart, mesEnd, anoEnd, idDevedor);
	}
	
}
