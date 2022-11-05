package com.minhascontas.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minhascontas.core.utils.Utilitarios;
import com.minhascontas.domain.dto.FaturaDto;
import com.minhascontas.domain.dto.ItemListaSaidaDto;
import com.minhascontas.domain.mapper.DefaultMapper;
import com.minhascontas.domain.model.CartaoCredito;
import com.minhascontas.domain.model.ContaBancaria;
import com.minhascontas.domain.model.Fatura;
import com.minhascontas.domain.model.Parcela;
import com.minhascontas.domain.model.Saida;
import com.minhascontas.domain.repository.CartaoCreditoRepository;
import com.minhascontas.domain.repository.ContaBancariaRepository;
import com.minhascontas.domain.repository.FaturaRepository;
import com.minhascontas.domain.repository.ParcelaRepository;
import com.minhascontas.domain.repository.SaidaRepository;
import com.minhascontas.domain.request.PagarFaturaRequest;
import com.minhascontas.domain.request.PagarParcelaRequest;
import com.minhascontas.domain.request.SaidaRequest;

@Service
public class SaidasService {

	@Autowired
	private SaidaRepository saidaRepo;
	
	@Autowired
	private FaturaRepository faturaRepo;

	@Autowired
	private CartaoCreditoRepository cartaoRepo;
	
	@Autowired
	private ContaBancariaRepository contaRepo;
	
	@Autowired
	private ParcelaRepository parcelaRepo;
	
	@Autowired
	private DefaultMapper mapper;


	public void novaSaida(SaidaRequest saida) {
		Saida novaSaida = mapper.saidaRequestToModel(saida);
		if(saida.getMeioPagto().equals("cartao")) {
			CartaoCredito cartao= cartaoRepo.findById(saida.getCartaoSelecionado()).get();
			List<Parcela> parcelas = gerarParcelasCartao(cartao, saida.getDataVencimento(), saida.getQtdeParcelas(), saida.getValor());
			novaSaida.setListaParcelas(parcelas);
			saidaRepo.save(novaSaida);
			atualizaValorFaturas(saida.getDataVencimento(), cartao, saida.getQtdeParcelas());
		}else {
			List<Parcela> parcelas = gerarParcelas(saida);
			novaSaida.setListaParcelas(parcelas);
			saidaRepo.save(novaSaida);
		}		
		
	}
	


	private List<Parcela> gerarParcelas(SaidaRequest saida) {
		List<Parcela> parcelas = new ArrayList<>();
		List<LocalDate> vencimentos = new ArrayList<>();
		ContaBancaria conta = new ContaBancaria();
		if(saida.getPago()) {
			conta = contaRepo.findById(saida.getIdConta()).get();			
		}
		
		//gerar lista de vencimentos
		LocalDate primeiroVencimento = Utilitarios.getDatasInicialFinalAtualLocalDate(saida.getDataVencimento()).get(2);
		for (int i = 0; i < saida.getQtdeParcelas(); i++) {
			vencimentos.add(primeiroVencimento.plusMonths(i));			
		}
		
		//gerar parcelas
		for (LocalDate vencimento : vencimentos) {
			Parcela p = new Parcela();
			p.setDataVencimento(vencimento);
			p.setValor(saida.getValor());
			if(saida.getPago()) {
				p.setSituacao("Pago");
				p.setValorPago(saida.getValor());
				p.setDataPagamento(vencimento);
				p.setConta(conta);
				atualizaSaldoConta(conta, saida.getValor());
			}
			parcelas.add(p);
		}
		
		return parcelas;
	}



	private List<Parcela> gerarParcelasCartao(CartaoCredito cartao, String dataVencimento, Integer qtdeParcelas, BigDecimal valor) {
		
		LocalDate dataPrimeiroVencimento = Utilitarios.getDataVencimentoCartaoLocalDate(dataVencimento, cartao.getDiaVencimento());
				
		//gerar lista de vencimentos das parcelas
		List<LocalDate> listaVencimentos = new ArrayList<>();
		for (Long i = 0L; i < qtdeParcelas; i++) {			
			listaVencimentos.add(dataPrimeiroVencimento.plusMonths(i));
		}
		
		//gerar faturas - armazena lista de faturas
		List<Fatura> faturas = gerarFaturas(listaVencimentos, cartao);
		
		//gerar parcelas
		List<Parcela> parcelas = criarParcelas(listaVencimentos, faturas ,valor);
		
		
		//relacionando parcela com sua fatura
		int cont =0;
		for(Fatura fatura : faturas) {
			List<Parcela> p = fatura.getItensFatura();
			p.add(parcelas.get(cont));
			cont++;
		}
	
		return parcelas;
	}


	private List<Fatura> gerarFaturas(List<LocalDate> vencimentos,  CartaoCredito cartao) {
		List<Fatura> faturas = new ArrayList<>();
		
		for(LocalDate vencimento : vencimentos) {
			Fatura fat = faturaRepo.findByCartaoIdAndDataVencimento(cartao.getId(), vencimento);
			if(fat == null) {
				Fatura novaFat = new Fatura();
				novaFat.setCartao(cartao);
				novaFat.setDataVencimento(vencimento);
				faturaRepo.save(novaFat);
				faturas.add(novaFat);
			} else {
				faturas.add(fat);
			}
		}
		
		return faturas;
	}
	
	private List<Parcela> criarParcelas(List<LocalDate> listaVencimentos, List<Fatura> faturas, BigDecimal valor) {
		
		List<Parcela> parcelas = new ArrayList<>();
		int cont = 0;
		for(LocalDate vencimento: listaVencimentos) {
			Parcela p = new Parcela();
			p.setFatura(faturas.get(cont));
			p.setDataVencimento(vencimento);
			p.setValor(valor);
			parcelas.add(p);
			cont++;
		}
		
		return parcelas;
	}
	
	private void atualizaValorFaturas(String dataVencimento, CartaoCredito cartao, Integer qtdeParcelas) {
		
		LocalDate dataPrimeiroVencimento = Utilitarios.getDataVencimentoCartaoLocalDate(dataVencimento, cartao.getDiaVencimento());
		List<LocalDate> listaVencimentos = new ArrayList<>();
		for (Long i = 0L; i < qtdeParcelas; i++) {			
			listaVencimentos.add(dataPrimeiroVencimento.plusMonths(i));
		}
		
		List<Fatura> faturas = new ArrayList<>();
		for(LocalDate vencimento : listaVencimentos) {
			Fatura fat = faturaRepo.findByCartaoIdAndDataVencimento(cartao.getId(), vencimento);
			if(fat != null) {
				faturas.add(fat);
			}
		}
		
		for(Fatura fatura : faturas) { 
			List<Parcela> parcelas = fatura.getItensFatura();
			BigDecimal  valorTotal = BigDecimal.ZERO;
			for(Parcela parcela: parcelas) {
				valorTotal = valorTotal.add(parcela.getValor());
			}
			fatura.setValor(valorTotal);
		}
		faturaRepo.saveAll(faturas);
		System.out.println(faturas);
		
	}



	public void pagarFatura(PagarFaturaRequest dadosPagto) {
		
		Fatura fatura = faturaRepo.findById(dadosPagto.getIdFatura()).orElseThrow();
		ContaBancaria conta = contaRepo.findById(dadosPagto.getIdConta()).orElseThrow();
		CartaoCredito cartao = fatura.getCartao();
		LocalDate dataPagamento = Utilitarios.getDatasInicialFinalAtualLocalDate(dadosPagto.getDataPagamento()).get(2);
		
		if(dadosPagto.getGerarParcelaComDiferenca() && dadosPagto.getValor().compareTo(fatura.getValor()) != 0) {
			// gerar parcela com a diferença do pagamento
			List<LocalDate> vencimento = new ArrayList<>();
			vencimento.add(fatura.getDataVencimento().plusMonths(1));
			String data = vencimento.get(0).toString();
			data = data.substring(0, 10);
			BigDecimal diferenca = fatura.getValor();
			diferenca = diferenca.subtract(dadosPagto.getValor());
			List<Parcela> parcelaDiferenca = gerarParcelasCartao(cartao, data, 1, diferenca);
			Saida novaSaida = new Saida();
			novaSaida.setListaParcelas(parcelaDiferenca);
			novaSaida.setMeioPagto("cartao");
			if(dadosPagto.getValor().compareTo(fatura.getValor()) == 1) {
				novaSaida.setNome("Crédito na fatura");
				novaSaida.setObs("Crédito referente ao pagamento a maior da fatura do mês anterior");
			}else {
				novaSaida.setNome("Débito na fatura");
				novaSaida.setObs("Diferença referente ao pagamento a menor da fatura do mês anterior. Se atente para corrigir o valor, pois incidirá juros da operadora do cartão");
			}
			saidaRepo.save(novaSaida);				
			atualizaValorFaturas(data, cartao, 1);
		}
		
		List<Parcela> parcelas = fatura.getItensFatura();
		
		for(Parcela parcela: parcelas) {
//			parcela.setConta(conta);
			parcela.setDataPagamento(dataPagamento);
			parcela.setSituacao("Pago");
			parcela.setValorPago(parcela.getValor());
		}
		//paga todos os itens da fatura
		parcelaRepo.saveAll(parcelas);		
		
		//atualiza o saldo da conta
		atualizaSaldoConta(conta, dadosPagto.getValor());
		System.out.println("Aqui");
		contaRepo.save(conta);
		
		//paga a fatura
		fatura.setDataPagamento(dataPagamento);
		fatura.setValorPago(dadosPagto.getValor());
		fatura.setSituacao(false);
		fatura.setConta(conta);
		faturaRepo.save(fatura);
	}
	
	private ContaBancaria atualizaSaldoConta(ContaBancaria conta, BigDecimal valor) {
		BigDecimal valorAtual = conta.getSaldo();
		valorAtual = valorAtual.subtract(valor);
		conta.setSaldo(valorAtual);		
		return conta;
	}



	public void pagarParcela(PagarParcelaRequest dadosPagto) {
		Parcela parcela = parcelaRepo.findById(dadosPagto.getIdParcela()).orElseThrow();
		ContaBancaria conta = contaRepo.findById(dadosPagto.getIdConta()).orElseThrow();
		LocalDate dataPagamento = Utilitarios.getDatasInicialFinalAtualLocalDate(dadosPagto.getDataPagamento()).get(2);
		
		parcela.setConta(conta);
		parcela.setDataPagamento(dataPagamento);
		parcela.setValorPago(dadosPagto.getValor());
		parcela.setSituacao("Pago");
		atualizaSaldoConta(conta, dadosPagto.getValor());
		parcelaRepo.save(parcela);		
		
	}



	public List<ItemListaSaidaDto> listarMensal(int mes) {
		// criar validação para ver se o mes é entre 1 - 12
		List<LocalDate> dataInicialDataFinal = Utilitarios.getDataInicialDataFinalLocalDate(mes);
		List<Parcela> parcelas = parcelaRepo.findByDataVencimentoBetween(dataInicialDataFinal.get(0), dataInicialDataFinal.get(1));
		List<ItemListaSaidaDto> response = new ArrayList<>();
		
		for(Parcela p: parcelas ) {
			ItemListaSaidaDto i = mapper.modelSaidaToDto(p);
			response.add(i);
		}
		
		response.forEach(System.out::println);
		return response;
	}



	public FaturaDto buscaFatura(Long idFatura) {
		Fatura fatura = faturaRepo.findById(idFatura).get();
		FaturaDto response = mapper.modelFaturaToDto(fatura);		
		return response;
	}


}
