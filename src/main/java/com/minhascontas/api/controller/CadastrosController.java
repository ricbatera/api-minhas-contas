package com.minhascontas.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.minhascontas.domain.dto.CartaoCreditoDto;
import com.minhascontas.domain.dto.ClassificacaoDto;
import com.minhascontas.domain.dto.ContaBancariaDto;
import com.minhascontas.domain.dto.DevedorDto;
import com.minhascontas.domain.request.CartaoCreditoRequest;
import com.minhascontas.domain.request.ClassificacaoRequest;
import com.minhascontas.domain.request.ContaBancariaRequest;
import com.minhascontas.domain.request.DevedorRequest;
import com.minhascontas.domain.service.CadastrosService;

@CrossOrigin
@RestController
@RequestMapping("/cadastros")
public class CadastrosController {

	@Autowired
	private CadastrosService cadastrosService;

	// CARTAO DE CREDITO
	@PostMapping("/cartao-credito/novoCartaoCredito")
	@ResponseStatus(code = HttpStatus.CREATED)
	public CartaoCreditoDto novoCartaoCredito(@RequestBody CartaoCreditoRequest cartao) {
		return cadastrosService.novoCartaoCredito(cartao);
	}

	@GetMapping("/cartao-credito/listar-cartoes-ativos")
	public List<CartaoCreditoDto> listarCartoesAtivos() {
		return cadastrosService.listarCartoesAtivos();
	}

	@GetMapping("/cartao-credito/listar-cartoes")
	public List<CartaoCreditoDto> listarCartoes() {
		return cadastrosService.listarCartoes();
	}

	@PutMapping("/cartao-credito/atualiza-cartao/{id}")
	public CartaoCreditoDto atualizaCartao(@RequestBody CartaoCreditoRequest atualizacao, @PathVariable Long id) {
		return cadastrosService.atualizaCartao(atualizacao, id);
	}

	// CONTAS BANCARIAS

	@PostMapping("/conta-bancaria/nova-conta-bancaria")
	@ResponseStatus(code = HttpStatus.CREATED)
	public ContaBancariaDto novaContaBancaria(@RequestBody ContaBancariaRequest conta) {
		return cadastrosService.novaConta(conta);
	}

	@GetMapping("/conta-bancaria/listar-contas-bancarias-ativas")
	public List<ContaBancariaDto> listarContasBancariasAtivas() {
		return cadastrosService.listarContasBancariasAtivas();
	}

	@GetMapping("/conta-bancaria/listar-contas-bancarias")
	public List<ContaBancariaDto> listarContasBancarias() {
		return cadastrosService.listarContasBancarias();
	}

	@PutMapping("/conta-bancaria/atualiza-conta-bancaria/{id}")
	public ContaBancariaDto atualizaContaBancaria(@RequestBody ContaBancariaRequest atualizacao,
			@PathVariable Long id) {
		return cadastrosService.atualizaContaBancaria(atualizacao, id);
	}

	// classificacao
	@PostMapping("/classificacao/nova-classificacao")
	@ResponseStatus(code = HttpStatus.CREATED)
	public ClassificacaoDto novaCLassificacao(@RequestBody ClassificacaoRequest payload) {
		return cadastrosService.novaClassificacao(payload);
	}

	@GetMapping("/classificacao/listar-classificacoes")
	public List<ClassificacaoDto> listarClassificacoes() {
		return cadastrosService.listarClassificacoes();
	}

	// devedor
	@PostMapping("/devedor/novo-devedor")
	@ResponseStatus(code = HttpStatus.CREATED)
	public DevedorDto novDevedor(@RequestBody DevedorRequest payload) {
		return cadastrosService.novoDevedor(payload);
	}

	@GetMapping("/devedor/listar-devedores")
	public List<DevedorDto> listarDevedores() {
		return cadastrosService.listarDevedores();
	}
	
	@DeleteMapping("/devedor/inativa-devedor")
	public void inativaDevedor(@Param(value = "idDevedor")Long idDevedor) {
		cadastrosService.inativaDevedor(idDevedor);
	}

}
