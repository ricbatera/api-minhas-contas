package com.minhascontas.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minhascontas.core.utils.Utilitarios;
import com.minhascontas.domain.dto.ItemListaEntradaDto;
import com.minhascontas.domain.mapper.DefaultMapper;
import com.minhascontas.domain.model.ContaBancaria;
import com.minhascontas.domain.model.Entrada;
import com.minhascontas.domain.model.ParcelaEntrada;
import com.minhascontas.domain.repository.ContaBancariaRepository;
import com.minhascontas.domain.repository.EntradaRepository;
import com.minhascontas.domain.repository.ParcelaEntradaRepository;
import com.minhascontas.domain.request.EntradaRequest;
import com.minhascontas.domain.request.ReceberEntradaRequest;

@Service
public class EntradasService {

	@Autowired
	EntradaRepository entradaRepo;

	@Autowired
	ParcelaEntradaRepository parcelaRepo;

	@Autowired
	private ContaBancariaRepository contaRepo;

	@Autowired
	private DefaultMapper mapper;

	public void novaEntrada(EntradaRequest payload) {
		Entrada novaEntrada = mapper.requestEntradaToModel(payload);

		// gera as parcelas
		novaEntrada.setListaParcelas(gerarParcelas(payload));

		// salva no banco
		entradaRepo.save(novaEntrada);

	}

	private List<ParcelaEntrada> gerarParcelas(EntradaRequest payload) {
		List<ParcelaEntrada> parcelas = new ArrayList<>();
		List<LocalDate> vencimentos = new ArrayList<>();

		// SE VIER MARCADO COMO RECEBIDO
		ContaBancaria conta = new ContaBancaria();
		if (payload.getRecebido()) {
			conta = contaRepo.findById(payload.getIdConta()).get();
		}
		// gerar lista de vencimentos
		LocalDate primeiroVencimento = Utilitarios
				.getDatasInicialFinalAtualLocalDate(payload.getDataPrevistaRecebimento()).get(2);
		for (int i = 0; i < payload.getQtdeParcelas(); i++) {
			vencimentos.add(primeiroVencimento.plusMonths(i));
		}

		// gerar parcelas
		for (LocalDate vencimento : vencimentos) {
			ParcelaEntrada p = new ParcelaEntrada();
			p.setDataPrevistaRecebimento(vencimento);
			p.setValor(payload.getValor());
			if (payload.getRecebido()) {
				p.setSituacao("Recebido");
				p.setConta(conta);
				p.setValorRecebido(payload.getValor());
				p.setDataRecebimento(vencimento);
				atualizaSaldoConta(conta, payload.getValor());
			}
			parcelas.add(p);
		}
		return parcelas;

	}

	private ContaBancaria atualizaSaldoConta(ContaBancaria conta, BigDecimal valor) {
		BigDecimal valorAtual = conta.getSaldo();
		valorAtual = valorAtual.add(valor);
		conta.setSaldo(valorAtual);
		contaRepo.save(conta);
		return conta;
	}

	public List<ItemListaEntradaDto> listarMensal(int mes) {
		// criar validação para ver se o mes é entre 1 - 12
		List<LocalDate> dataInicialDataFinal = Utilitarios.getDataInicialDataFinalLocalDate(mes);
		List<ParcelaEntrada> parcelas = parcelaRepo.findByDataPrevistaRecebimentoBetween(dataInicialDataFinal.get(0),
				dataInicialDataFinal.get(1));
		List<ItemListaEntradaDto> response = new ArrayList<>();

		for (ParcelaEntrada p : parcelas) {
			ItemListaEntradaDto i = mapper.modelToItemListaEntradaDto(p);
			response.add(i);
		}
		return response;
	}

	public void pagarParcela(ReceberEntradaRequest payload) {
		ParcelaEntrada p = parcelaRepo.findById(payload.getIdParcela()).get();
		ContaBancaria conta = contaRepo.findById(payload.getIdConta()).get();
		LocalDate dataRecebimento = Utilitarios.getDatasInicialFinalAtualLocalDate(payload.getDataRecebimento()).get(2);
		p.setSituacao("Recebido");
		p.setConta(conta);
		p.setValorRecebido(payload.getValor());
		p.setDataRecebimento(dataRecebimento);
		parcelaRepo.save(p);
		atualizaSaldoConta(conta, payload.getValor());
	}

}
