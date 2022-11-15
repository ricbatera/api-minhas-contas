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

import com.minhascontas.domain.dto.ItemListaEntradaDto;
import com.minhascontas.domain.request.EntradaRequest;
import com.minhascontas.domain.request.ReceberEntradaRequest;
import com.minhascontas.domain.service.EntradasService;

@CrossOrigin
@RestController
@RequestMapping("/entradas")
public class EntradasController {
	
	@Autowired
	private EntradasService entradaService;
	
	//CARTAO DE CREDITO
	@PostMapping("/nova-entrada")
	@ResponseStatus(code = HttpStatus.CREATED)
	public EntradaRequest novaSaida(@RequestBody EntradaRequest payload) {
		entradaService.novaEntrada(payload);
		return payload;
	}
	
	@GetMapping("/listar-mensal")
	public List<ItemListaEntradaDto> listarSaidasMensal(@Param(value = "mes") int mes, @Param(value = "ano") int ano) {
		return entradaService.listarMensal(mes, ano);
	}
	
	@PostMapping("/receber-parcela")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void pagarParcela(@RequestBody ReceberEntradaRequest payload) {
		entradaService.pagarParcela(payload);		
	}
//	
//	@PostMapping("/pagar-fatura")
//	@ResponseStatus(code = HttpStatus.NO_CONTENT)
//	public void pagarFatura(@RequestBody PagarFaturaRequest dadosPagto) {
//		saidasService.pagarFatura(dadosPagto);
//	}
//	
//	@PostMapping("/pagar-parcela")
//	@ResponseStatus(code = HttpStatus.NO_CONTENT)
//	public void pagarParcela(@RequestBody PagarParcelaRequest dadosPagto) {
//		saidasService.pagarParcela(dadosPagto);		
//	}
//	
//	@GetMapping("/listar-mensal")
//	public List<ItemListaSaidaDto> listarSaidasMensal(@Param(value = "mes") int mes) {
//		return saidasService.listarMensal(mes);
//	}
//	
//	@GetMapping("/busca-fatura")
//	public FaturaDto buscaFatura(@Param(value = "idFatura") Long idFatura) {
//		return saidasService.buscaFatura(idFatura);
//	}
}
