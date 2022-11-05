package com.minhascontas.domain.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minhascontas.domain.dto.CartaoCreditoDto;
import com.minhascontas.domain.dto.ContaBancariaDto;
import com.minhascontas.domain.mapper.DefaultMapper;
import com.minhascontas.domain.model.CartaoCredito;
import com.minhascontas.domain.model.ContaBancaria;
import com.minhascontas.domain.model.Fatura;
import com.minhascontas.domain.model.Parcela;
import com.minhascontas.domain.repository.CartaoCreditoRepository;
import com.minhascontas.domain.repository.ContaBancariaRepository;
import com.minhascontas.domain.repository.FaturaRepository;
import com.minhascontas.domain.request.CartaoCreditoRequest;
import com.minhascontas.domain.request.ContaBancariaRequest;

@Service
public class CadastrosService {

	@Autowired
	private CartaoCreditoRepository cartaoRepo;
	
	@Autowired
	private FaturaRepository faturaRepo;

	@Autowired
	private ContaBancariaRepository contaRepo;

	@Autowired
	private DefaultMapper mapper;

//	CARTAO DE CREDITO
	public CartaoCreditoDto novoCartaoCredito(CartaoCreditoRequest cartao) {
		CartaoCredito novoCartao = mapper.cartaoToModel(cartao);
		novoCartao = cartaoRepo.save(novoCartao);
		CartaoCreditoDto response = mapper.modelToDto(novoCartao);
//		response.setDataAlteracao(novoCartao.getDados().getDataAlteracao());
		System.out.println(novoCartao.toString());
		return response;
	}

	public List<CartaoCreditoDto> listarCartoes() {
		List<CartaoCredito> lista = cartaoRepo.findAll();
		List<CartaoCreditoDto> listResponse = new ArrayList<>();

		for (CartaoCredito item : lista) {
			listResponse.add(mapper.modelToDto(item));

		}
		return listResponse;
	}

	public List<CartaoCreditoDto> listarCartoesAtivos() {
		List<CartaoCredito> lista = cartaoRepo.findByStatus(true);
		List<CartaoCreditoDto> listResponse = new ArrayList<>();

		for (CartaoCredito item : lista) {
			listResponse.add(mapper.modelToDto(item));

		}
		return listResponse;
	}

	public CartaoCreditoDto atualizaCartao(CartaoCreditoRequest atualizacao, Long id) {
		CartaoCredito cartao = mapper.cartaoToModel(atualizacao);
		CartaoCredito cartaoBase = cartaoRepo.findById(id).orElseThrow();
		Boolean validaAlteracaoDiaVencimento = false;

		if (atualizacao.getDiaVencimento() != cartaoBase.getDiaVencimento()) {
			//faz uma pre-verificação se houve mudança no dia do vencimento
			validaAlteracaoDiaVencimento = true;
		}

		BeanUtils.copyProperties(cartao, cartaoBase, "id", "dados");
		cartaoRepo.save(cartaoBase);
		if (validaAlteracaoDiaVencimento) {
			//apos salvar no banco as alterações chama a atualizações de parcelas
			atualizaVencimentosCartao(cartaoBase);
		}
			return mapper.modelToDto(cartaoBase);
	}
	
	private void atualizaVencimentosCartao(CartaoCredito cartao) {
		List<Fatura> faturas = faturaRepo.findByCartaoIdAndSituacao(cartao.getId(), true);
		for(Fatura fat : faturas) {
			fat.setDataVencimento(fat.getDataVencimento().withDayOfMonth(cartao.getDiaVencimento()));
			List<Parcela> p = fat.getItensFatura();
			for(Parcela pa: p) {
				pa.setDataVencimento(fat.getDataVencimento());
			}
		}
		faturaRepo.saveAll(faturas);
		
	}

	// CONTAS BANCARIAS

	public ContaBancariaDto novaConta(ContaBancariaRequest conta) {
		ContaBancaria novaConta = mapper.contaBancariaRequestToModel(conta);
		contaRepo.save(novaConta);
		ContaBancariaDto response = mapper.contaBancariaModelToDto(novaConta);
		return response;
	}

	public List<ContaBancariaDto> listarContasBancarias() {
		List<ContaBancaria> lista = contaRepo.findAll();
		List<ContaBancariaDto> listResponse = new ArrayList<>();

		for (ContaBancaria item : lista) {
			listResponse.add(mapper.contaBancariaModelToDto(item));
		}
		return listResponse;
	}

	public List<ContaBancariaDto> listarContasBancariasAtivas() {
		List<ContaBancaria> lista = contaRepo.findByStatus(true);
		List<ContaBancariaDto> listResponse = new ArrayList<>();

		for (ContaBancaria item : lista) {
			listResponse.add(mapper.contaBancariaModelToDto(item));
		}
		return listResponse;
	}

	public ContaBancariaDto atualizaContaBancaria(ContaBancariaRequest atualizacao, Long id) {
		ContaBancaria conta = mapper.contaBancariaRequestToModel(atualizacao);
		ContaBancaria contaBase = contaRepo.findById(id).orElseThrow();
		BeanUtils.copyProperties(conta, contaBase, "id", "dados");
		contaRepo.save(contaBase);
		return mapper.contaBancariaModelToDto(contaBase);
	}

}
