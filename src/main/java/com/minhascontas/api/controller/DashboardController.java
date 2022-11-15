package com.minhascontas.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minhascontas.domain.dto.DashboardDto;
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
	
	
}
