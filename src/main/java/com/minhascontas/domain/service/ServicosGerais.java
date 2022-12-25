package com.minhascontas.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minhascontas.core.utils.Utilitarios;
import com.minhascontas.domain.dto.FaturaDto;
import com.minhascontas.domain.dto.ItemListaSaidaDto;
import com.minhascontas.domain.dto.SaidaDto;
import com.minhascontas.domain.mapper.DefaultMapper;
import com.minhascontas.domain.model.CartaoCredito;
import com.minhascontas.domain.model.Classificacao;
import com.minhascontas.domain.model.ContaBancaria;
import com.minhascontas.domain.model.Devedor;
import com.minhascontas.domain.model.Fatura;
import com.minhascontas.domain.model.Parcela;
import com.minhascontas.domain.model.Saida;
import com.minhascontas.domain.repository.CartaoCreditoRepository;
import com.minhascontas.domain.repository.ClassificacaoRepository;
import com.minhascontas.domain.repository.ContaBancariaRepository;
import com.minhascontas.domain.repository.DevedorRepository;
import com.minhascontas.domain.repository.FaturaRepository;
import com.minhascontas.domain.repository.ParcelaRepository;
import com.minhascontas.domain.repository.SaidaRepository;
import com.minhascontas.domain.request.EntradaRequest;
import com.minhascontas.domain.request.PagarFaturaRequest;
import com.minhascontas.domain.request.PagarParcelaRequest;
import com.minhascontas.domain.request.SaidaRequest;

@Service
public class ServicosGerais {

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
	private ClassificacaoRepository classificacaoRepo;
	
	@Autowired
	private EntradasService entradaService;
	
	@Autowired
	private DevedorRepository devedorRepo;
	
	@Autowired
	private DefaultMapper mapper;


	public void novaSaida(SaidaRequest saida) {
		Saida novaSaida = mapper.saidaRequestToModel(saida);
		if(saida.getMeioPagto().equals("cartao")) {			
			CartaoCredito cartao= cartaoRepo.findById(saida.getCartaoSelecionado()).get();
			List<Parcela> parcelas = gerarParcelasCartao(saida, cartao, null);
			novaSaida.setListaParcelas(parcelas);
			saidaRepo.save(novaSaida);
			atualizaValorFaturas(saida.getDataVencimento(), cartao, saida.getQtdeParcelas());
			
		}else {
			List<Parcela> parcelas = gerarParcelas(saida);
			novaSaida.setListaParcelas(parcelas);
			saidaRepo.save(novaSaida);
		}
		// gera as entradas de devedores
		if(saida.getCriaEntrada()) {
			geraEntradaDevedor(saida);
		}
		
	}

	private List<Parcela> gerarParcelas(SaidaRequest saida) {
		List<Parcela> parcelas = new ArrayList<>();
		List<LocalDate> vencimentos = new ArrayList<>();
		ContaBancaria conta = new ContaBancaria();
		Classificacao c = classificacaoRepo.findById(saida.getClassificacaoId()).get();
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
			p.setClassificacao(c);
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



	private List<Parcela> gerarParcelasCartao(SaidaRequest s, CartaoCredito cartao, PagarFaturaRequest pf) {
		
		//gerar lista de vencimentos das parcelas
		List<LocalDate> listaVencimentos = new ArrayList<>();
		
		if(s!=null) {
			LocalDate dataPrimeiroVencimento = Utilitarios.getDataVencimentoCartaoLocalDate(s.getDataVencimento(), cartao.getDiaVencimento());
			for (Long i = 0L; i < s.getQtdeParcelas(); i++) {			
				listaVencimentos.add(dataPrimeiroVencimento.plusMonths(i));
			}			
		}else {
			LocalDate dataPrimeiroVencimento = Utilitarios.getDataVencimentoCartaoLocalDate(pf.getDataPagamento(), cartao.getDiaVencimento());
			
			for (Long i = 0L; i < 1; i++) {			
				listaVencimentos.add(dataPrimeiroVencimento.plusMonths(i));
			}
		}
				
		
		//gerar faturas - armazena lista de faturas
		List<Fatura> faturas = gerarFaturas(listaVencimentos, cartao);
		
		//gerar parcelas
		List<Parcela> parcelas = criarParcelas(listaVencimentos, faturas , s, pf);
		
		
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
	
	private List<Parcela> criarParcelas(List<LocalDate> listaVencimentos, List<Fatura> faturas, SaidaRequest s, PagarFaturaRequest pf) {
		Classificacao c = null;
		BigDecimal valor = BigDecimal.ZERO;
		Devedor d = null;
		
		if(s != null) {
			c = classificacaoRepo.findById(s.getClassificacaoId()).get();
			valor = valor.add(s.getValor());
			if(s.getAssociaDevedor()) {
				d = devedorRepo.findById(s.getDevedorId()).get();
			}
		}
		
		if(pf != null) {
			c = classificacaoRepo.findById(pf.getClassificacaoId()).get();
			valor = valor.add(pf.getValor());
			if(pf.getAssociaDevedor()) {
				d = devedorRepo.findById(pf.getDevedorId()).get();
			}
		}
		
		List<Parcela> parcelas = new ArrayList<>();
		int cont = 0;
		for(LocalDate vencimento: listaVencimentos) {
			Parcela p = new Parcela();
			p.setFatura(faturas.get(cont));
			p.setDataVencimento(vencimento);
			p.setValor(valor);
			p.setClassificacao(c);				
			p.setDevedor(d);
			p.setContagemParcelas((cont + 1) + "/"+ listaVencimentos.size());
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
		
		// criar algo para gerar classificação de devedores automática
		
		if(dadosPagto.getGerarParcelaComDiferenca() && dadosPagto.getValor().compareTo(fatura.getValor()) != 0) {
			// gerar parcela com a diferença do pagamento
			List<LocalDate> vencimento = new ArrayList<>();
			vencimento.add(fatura.getDataVencimento().plusMonths(1));
			String data = vencimento.get(0).toString();
			data = data.substring(0, 10);
			BigDecimal diferenca = fatura.getValor();
			diferenca = diferenca.subtract(dadosPagto.getValor());
			dadosPagto.setDataPagamento(data);
			dadosPagto.setValor(diferenca);
			List<Parcela> parcelaDiferenca = gerarParcelasCartao(null, cartao, dadosPagto); // estudar aqui qual item mandar como categoria
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
			parcela.setConta(conta);
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



	public List<ItemListaSaidaDto> listarMensal(int mes, int ano) {
		// criar validação para ver se o mes é entre 1 - 12
		List<LocalDate> dataInicialDataFinal = Utilitarios.getDataInicialDataFinalLocalDateComAno(mes, ano);
		List<Parcela> parcelas = parcelaRepo.findByDataVencimentoBetween(dataInicialDataFinal.get(0), dataInicialDataFinal.get(1));
		List<ItemListaSaidaDto> response = new ArrayList<>();
		
		for(Parcela p: parcelas ) {
			ItemListaSaidaDto i = mapper.modelSaidaToDto(p);
			response.add(i);
		}
		return response;
	}



	public FaturaDto buscaFatura(Long idFatura) {
		Fatura fatura = faturaRepo.findById(idFatura).get();
		FaturaDto response = mapper.modelFaturaToDto(fatura);
		return response;
	}

	private void geraEntradaDevedor(SaidaRequest saida) {
		
		EntradaRequest e = new EntradaRequest();
		
		e.setAssociaDevedor(saida.getAssociaDevedor());
		e.setClassificacaoId(saida.getClassificacaoId());
		e.setDataPrevistaRecebimento(saida.getDataVencimento());
		e.setDevedorId(saida.getDevedorId());
		e.setQtdeParcelas(saida.getQtdeParcelas());
		e.setRecebido(false);// implementar o recebido sim ou não nas entradas
		e.setNome(saida.getNome() + ". Entrada Automática ");
		e.setObs(saida.getObs() + ". ENTRADA AUTOMÁTICA GERADA PELO SISTEMA");
		e.setValor(saida.getValorEntrada());
		entradaService.novaEntrada(e);		
	}

	public SaidaDto buscaSaidaById(Long id) {
		Saida s = saidaRepo.findById(id).get();
		SaidaDto sd = mapper.modelToSaidaDto(s);
		List<Parcela> lista = s.getListaParcelas();
		
		List<Parcela> pagas =  lista.stream()
		.filter(p-> p.getSituacao().equals("Pago"))
		.collect(Collectors.toList());
		
		BigDecimal total = lista.stream()
				.map(p -> p.getValor())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		if(lista.get(0).getFatura() != null) {
			sd.setCartao(lista.get(0).getFatura().getCartao());
		}
		sd.setXDeParcelas(String.valueOf(pagas.size()) +"/"+ String.valueOf(lista.size()));
		sd.setTotal(total);
		return sd;
	}

	public void ajustar(Long id) {
		List<Parcela> parcelas = parcelaRepo.findBySaidaId(id);
		
		int cont = 1;
		
		for(Parcela p : parcelas) {
			p.setContagemParcelas(cont + "/" + parcelas.size());
			cont ++;
		}
		
		parcelaRepo.saveAll(parcelas);
		
	}
	
	public void ajustarTodos() {
		List<Parcela> parcelas = parcelaRepo.findAll();
		
		List<Long> ids = parcelas.stream()
				.map(p -> p.getSaida().getId())
				.collect(Collectors.toList());
		
		Set<Long> set = new LinkedHashSet<>();
		set.addAll(ids);
		ids.clear();
		ids.addAll(set);
		
		for(Long id : ids) {
			int cont = 1;
			List<Parcela> parcelaList = parcelaRepo.findBySaidaId(id);
			for(Parcela p : parcelaList) {
				p.setContagemParcelas(cont + "/" + parcelaList.size());
				cont ++;
			}
			parcelaRepo.saveAll(parcelaList);
		}		
	}
	
	public void atualizaTodasFaturas() {
		List<Fatura> faturas = faturaRepo.findAll();
		
		for(Fatura fatura : faturas) { 
			List<Parcela> parcelas = fatura.getItensFatura();
			BigDecimal  valorTotal = BigDecimal.ZERO;
			for(Parcela parcela: parcelas) {
				valorTotal = valorTotal.add(parcela.getValor());
			}
			fatura.setValor(valorTotal);
		}
		faturaRepo.saveAll(faturas);		
	}
}
