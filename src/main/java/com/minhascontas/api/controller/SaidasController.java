package com.minhascontas.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.minhascontas.domain.dto.FaturaDto;
import com.minhascontas.domain.dto.ItemListaSaidaDto;
import com.minhascontas.domain.request.PagarFaturaRequest;
import com.minhascontas.domain.request.PagarParcelaRequest;
import com.minhascontas.domain.request.SaidaRequest;
import com.minhascontas.domain.service.SaidasService;

@CrossOrigin
@RestController
@RequestMapping("/saidas")
public class SaidasController {
	
	@Autowired
	private SaidasService saidasService;
	
	//CARTAO DE CREDITO
	@PostMapping("/nova-saida")
	@ResponseStatus(code = HttpStatus.CREATED)
	public SaidaRequest novaSaida(@RequestBody SaidaRequest saida) {
		saidasService.novaSaida(saida);
		return saida;
	}
	
	@PostMapping("/pagar-fatura")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void pagarFatura(@RequestBody PagarFaturaRequest dadosPagto) {
		saidasService.pagarFatura(dadosPagto);
	}
	
	@PostMapping("/pagar-parcela")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void pagarParcela(@RequestBody PagarParcelaRequest dadosPagto) {
		saidasService.pagarParcela(dadosPagto);		
	}
	
	@GetMapping("/listar-mensal")
	public List<ItemListaSaidaDto> listarSaidasMensal(@Param(value = "mes") int mes, @Param(value = "ano") int ano) {
		return saidasService.listarMensal(mes, ano);
	}
	
	@GetMapping("/busca-fatura")
	public FaturaDto buscaFatura(@Param(value = "idFatura") Long idFatura) {
		return saidasService.buscaFatura(idFatura);
	}
}
