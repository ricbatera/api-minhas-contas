package com.minhascontas.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minhascontas.domain.model.CartaoCredito;
import com.minhascontas.domain.request.CartaoCreditoRequest;
import com.minhascontas.domain.service.CadastrosService;

@CrossOrigin
@RestController
@RequestMapping("/cadastro")
public class CadastrosController {
	
	@Autowired
	private CadastrosService cadastrosService;
	
	@PostMapping("/novoCartaoCredito")
	public CartaoCredito novoCartaoCredito(@RequestBody CartaoCreditoRequest cartao) {
		return cadastrosService.novoCartaoCredito(cartao);		
	}

}
