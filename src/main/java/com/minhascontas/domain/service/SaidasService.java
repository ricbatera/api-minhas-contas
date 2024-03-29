package com.minhascontas.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minhascontas.core.utils.Utilitarios;
import com.minhascontas.domain.dto.EditaSaidaDto;
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
import com.minhascontas.domain.model.SaldoBancario;
import com.minhascontas.domain.model.Tag;
import com.minhascontas.domain.repository.CartaoCreditoRepository;
import com.minhascontas.domain.repository.ClassificacaoRepository;
import com.minhascontas.domain.repository.ContaBancariaRepository;
import com.minhascontas.domain.repository.DevedorRepository;
import com.minhascontas.domain.repository.FaturaRepository;
import com.minhascontas.domain.repository.ParcelaRepository;
import com.minhascontas.domain.repository.SaidaRepository;
import com.minhascontas.domain.repository.SaldoBancarioRepository;
import com.minhascontas.domain.repository.TagRepository;
import com.minhascontas.domain.request.AtualizaParcelasRequest;
import com.minhascontas.domain.request.DeletarParcelaRequest;
import com.minhascontas.domain.request.EntradaRequest;
import com.minhascontas.domain.request.PagarFaturaRequest;
import com.minhascontas.domain.request.PagarParcelaRequest;
import com.minhascontas.domain.request.SaidaRequest;

@Service
public class SaidasService {

	@Autowired
	private SaidaRepository saidaRepo;

	@Autowired
	private SaldoBancarioRepository saldoBancarioRepo;

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
	private TagRepository tagRepo;

	@Autowired
	private DefaultMapper mapper;
	

	public void novaSaida(SaidaRequest saida) {
		Saida novaSaida = mapper.saidaRequestToModel(saida);
		if (saida.getDataCompra() != null && !(saida.getDataCompra().isEmpty())) {
			novaSaida.setDataCompra(LocalDate.parse(saida.getDataCompra()));
		}
		if (saida.getMeioPagto().equals("cartao")) {
			CartaoCredito cartao = cartaoRepo.findById(saida.getCartaoSelecionado()).get();
			List<Parcela> parcelas = gerarParcelasCartao(saida, cartao, null);
			novaSaida.setListaParcelas(parcelas);
			saidaRepo.save(novaSaida);
			if (saida.getDataCompra() != null && !(saida.getDataCompra().isEmpty())) {
				atualizaValorFaturas(saida.getDataCompra(), cartao, saida.getQtdeParcelas(), true);
			} else {
				atualizaValorFaturas(saida.getDataVencimento(), cartao, saida.getQtdeParcelas(), false);
			}

		} else {
			List<Parcela> parcelas = gerarParcelas(saida);
			novaSaida.setListaParcelas(parcelas);
			saidaRepo.save(novaSaida);
		}
		// gera as entradas de devedores
		if (saida.getCriaEntrada()) {
			geraEntradaDevedor(saida);
		}

	}

	private List<Parcela> gerarParcelas(SaidaRequest saida) {
		List<Parcela> parcelas = new ArrayList<>();
		List<LocalDate> vencimentos = new ArrayList<>();
		ContaBancaria conta = new ContaBancaria();
		Classificacao c = classificacaoRepo.findById(saida.getClassificacaoId()).get();
		if (saida.getPago()) {
			conta = contaRepo.findById(saida.getIdConta()).get();
		}

		// gerar lista de vencimentos
		LocalDate primeiroVencimento = Utilitarios.getDatasInicialFinalAtualLocalDate(saida.getDataVencimento()).get(2);
		for (int i = 0; i < saida.getQtdeParcelas(); i++) {
			vencimentos.add(primeiroVencimento.plusMonths(i));
		}

		// gerar parcelas
		List<Long> t = saida.getTags();
		for (LocalDate vencimento : vencimentos) {
			List<Classificacao> tagLis = new ArrayList<>();
			Parcela p = new Parcela();
			p.setDataVencimento(vencimento);
			p.setValor(saida.getValor());
			p.setClassificacao(c);
			for(Long tag: t) {
				tagLis.add(classificacaoRepo.findById(tag).get());
			}
			p.setListaTags(tagLis);
			if (saida.getPago()) {
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

		// gerar lista de vencimentos das parcelas
		List<LocalDate> listaVencimentos = new ArrayList<>();

		if (s != null) {
			LocalDate dataPrimeiroVencimento = LocalDate.now();
			if (s.getDataCompra() != null && !(s.getDataCompra().isEmpty())) {
				dataPrimeiroVencimento = Utilitarios.getDataVencimentoCartaoLocalDateByDataCompra(s.getDataCompra(),
						cartao.getDiaVencimento());
			} else {
				dataPrimeiroVencimento = Utilitarios.getDataVencimentoCartaoLocalDate(s.getDataVencimento(),
						cartao.getDiaVencimento());
			}
			for (Long i = 0L; i < s.getQtdeParcelas(); i++) {
				listaVencimentos.add(dataPrimeiroVencimento.plusMonths(i));
			}
		} else {
			LocalDate dataPrimeiroVencimento = Utilitarios.getDataVencimentoCartaoLocalDate(pf.getDataPagamento(),
					cartao.getDiaVencimento());

			for (Long i = 0L; i < 1; i++) {
				listaVencimentos.add(dataPrimeiroVencimento.plusMonths(i));
			}
		}

		// gerar faturas - armazena lista de faturas
		List<Fatura> faturas = gerarFaturas(listaVencimentos, cartao);

		// gerar parcelas
		List<Parcela> parcelas = criarParcelas(listaVencimentos, faturas, s, pf);

		// relacionando parcela com sua fatura
		int cont = 0;
		for (Fatura fatura : faturas) {
			List<Parcela> p = fatura.getItensFatura();
			p.add(parcelas.get(cont));
			cont++;
		}

		return parcelas;
	}

	private List<Fatura> gerarFaturas(List<LocalDate> vencimentos, CartaoCredito cartao) {
		List<Fatura> faturas = new ArrayList<>();

		for (LocalDate vencimento : vencimentos) {
			Fatura fat = faturaRepo.findByCartaoIdAndDataVencimento(cartao.getId(), vencimento);
			if (fat == null) {
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

	private List<Parcela> criarParcelas(List<LocalDate> listaVencimentos, List<Fatura> faturas, SaidaRequest s,
			PagarFaturaRequest pf) {
		Classificacao c = null;
		BigDecimal valor = BigDecimal.ZERO;
		Devedor d = null;

		if (s != null) {
			c = classificacaoRepo.findById(s.getClassificacaoId()).get();
			valor = valor.add(s.getValor());
			if (s.getAssociaDevedor()) {
				d = devedorRepo.findById(s.getDevedorId()).get();
			}
		}

		if (pf != null) {
			c = classificacaoRepo.findById(pf.getClassificacaoId()).get();
			valor = valor.add(pf.getValor());
			if (pf.getAssociaDevedor()) {
				d = devedorRepo.findById(pf.getDevedorId()).get();
			}
		}

		List<Parcela> parcelas = new ArrayList<>();
		List<Long> tagIdList = s.getTags();
		int cont = 0;
		for (LocalDate vencimento : listaVencimentos) {
			List<Classificacao> tags = new ArrayList<>();
			Parcela p = new Parcela();
			for(Long tag: tagIdList) {
				tags.add(classificacaoRepo.findById(tag).get());				
			}
			p.setListaTags(tags);
			p.setFatura(faturas.get(cont));
			p.setDataVencimento(vencimento);
			p.setValor(valor);
			p.setClassificacao(c);
			p.setDevedor(d);
			p.setContagemParcelas((cont + 1) + "/" + listaVencimentos.size());
			parcelas.add(p);
			cont++;
		}

		return parcelas;
	}

	private void atualizaValorFaturas(String dataVencimento, CartaoCredito cartao, Integer qtdeParcelas,
			boolean dataCompra) {
		LocalDate dataPrimeiroVencimento = LocalDate.now();
		if (dataCompra) {
			dataPrimeiroVencimento = Utilitarios.getDataVencimentoCartaoLocalDateByDataCompra(dataVencimento,
					cartao.getDiaVencimento());
		} else {
			dataPrimeiroVencimento = Utilitarios.getDataVencimentoCartaoLocalDate(dataVencimento,
					cartao.getDiaVencimento());
		}

		List<LocalDate> listaVencimentos = new ArrayList<>();
		for (Long i = 0L; i < qtdeParcelas; i++) {
			listaVencimentos.add(dataPrimeiroVencimento.plusMonths(i));
		}

		List<Fatura> faturas = new ArrayList<>();
		for (LocalDate vencimento : listaVencimentos) {
			Fatura fat = faturaRepo.findByCartaoIdAndDataVencimento(cartao.getId(), vencimento);
			if (fat != null) {
				faturas.add(fat);
			}
		}

		for (Fatura fatura : faturas) {
			List<Parcela> parcelas = fatura.getItensFatura();
			BigDecimal valorTotal = BigDecimal.ZERO;
			for (Parcela parcela : parcelas) {
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

		if (dadosPagto.getGerarParcelaComDiferenca() && dadosPagto.getValor().compareTo(fatura.getValor()) != 0) {
			// gerar parcela com a diferença do pagamento
			List<LocalDate> vencimento = new ArrayList<>();
			vencimento.add(fatura.getDataVencimento().plusMonths(1));
			String data = vencimento.get(0).toString();
			data = data.substring(0, 10);
			BigDecimal diferenca = fatura.getValor();
			diferenca = diferenca.subtract(dadosPagto.getValor());
			dadosPagto.setDataPagamento(data);
			dadosPagto.setValor(diferenca);
			List<Parcela> parcelaDiferenca = gerarParcelasCartao(null, cartao, dadosPagto); // estudar aqui qual item
																							// mandar como categoria
			Saida novaSaida = new Saida();
			novaSaida.setListaParcelas(parcelaDiferenca);
			novaSaida.setMeioPagto("cartao");
			if (dadosPagto.getValor().compareTo(fatura.getValor()) == 1) {
				novaSaida.setNome("Crédito na fatura");
				novaSaida.setObs("Crédito referente ao pagamento a maior da fatura do mês anterior");
			} else {
				novaSaida.setNome("Débito na fatura");
				novaSaida.setObs(
						"Diferença referente ao pagamento a menor da fatura do mês anterior. Se atente para corrigir o valor, pois incidirá juros da operadora do cartão");
			}
			saidaRepo.save(novaSaida);
			atualizaValorFaturas(data, cartao, 1, false);
		}

		List<Parcela> parcelas = fatura.getItensFatura();

		for (Parcela parcela : parcelas) {
			parcela.setConta(conta);
			parcela.setDataPagamento(dataPagamento);
			parcela.setSituacao("Pago");
			parcela.setValorPago(parcela.getValor());
		}
		// paga todos os itens da fatura
		parcelaRepo.saveAll(parcelas);

		// atualiza o saldo da conta
		atualizaSaldoConta(conta, dadosPagto.getValor());
		contaRepo.save(conta);

		// paga a fatura
		fatura.setDataPagamento(dataPagamento);
		fatura.setValorPago(dadosPagto.getValor());
		fatura.setSituacao(false);
		fatura.setConta(conta);
		faturaRepo.save(fatura);

		// atualizar os saldo bancario
		atualizaSaldoBancario(conta, dadosPagto, null);
	}

	private void atualizaSaldoBancario(ContaBancaria conta, PagarFaturaRequest dadosPagto,
			PagarParcelaRequest dadosPagto2) {
		SaldoBancario sb = new SaldoBancario();
		if (dadosPagto != null) {
			sb.setConta(conta);
			sb.setDataTransacao(LocalDate.parse(dadosPagto.getDataPagamento()));
			sb.setValor(dadosPagto.getValor());
			sb.setTipo("Saída");
		}

		if (dadosPagto2 != null) {
			sb.setConta(conta);
			sb.setDataTransacao(LocalDate.parse(dadosPagto2.getDataPagamento()));
			sb.setValor(dadosPagto2.getValor());
			sb.setTipo("Saída");
		}

		saldoBancarioRepo.save(sb);
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

		// atualiza saldo bancario
		atualizaSaldoBancario(conta, null, dadosPagto);

	}

	public List<ItemListaSaidaDto> listarMensal(int mes, int ano, String tags) {
		List<Long> idTags = new ArrayList<>();
		if(!(tags.equals("All"))) {
			idTags = Utilitarios.splitStringPorTracoToLong(tags);			
		}		
		
		// criar validação para ver se o mes é entre 1 - 12
		List<LocalDate> dataInicialDataFinal = Utilitarios.getDataInicialDataFinalLocalDateComAno(mes, ano);
		List<Parcela> parcelas = parcelaRepo.findByDataVencimentoBetween(dataInicialDataFinal.get(0),
				dataInicialDataFinal.get(1));
		List<ItemListaSaidaDto> response = new ArrayList<>();
		List<Parcela> parcelasFiltradas = new ArrayList<>();
		parcelasFiltradas.addAll(parcelas);
		if(!idTags.isEmpty()) {
			parcelasFiltradas.clear();
			for(Long idTag: idTags) {
				for(Parcela p : parcelas) {
					List<Classificacao> tagsLi = p.getListaTags();
					for(Classificacao tt: tagsLi) {
						if(tt.getId() == idTag) {
							parcelasFiltradas.add(p);
						}
					}
				}
			}
		}
		List<Parcela>pf = parcelasFiltradas.stream().distinct().collect(Collectors.toList());
		for (Parcela p : pf) {
			ItemListaSaidaDto i = mapper.modelSaidaToDto(p);
			if (p.getDevedor() != null) {
				i.setDevedorNome(p.getDevedor().getNome());
			} else {
				i.setDevedorNome("Minha");
			}
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
		sd.setTags(lista.get(0).getListaTags());
		
		List<Parcela> pagas = lista.stream().filter(p -> p.getSituacao().equals("Pago")).collect(Collectors.toList());

		BigDecimal total = lista.stream().map(p -> p.getValor()).reduce(BigDecimal.ZERO, BigDecimal::add);
		if (lista.get(0).getFatura() != null) {
			sd.setCartao(lista.get(0).getFatura().getCartao());
		}
		sd.setXDeParcelas(String.valueOf(pagas.size()) + "/" + String.valueOf(lista.size()));
		sd.setTotal(total);
		sd.setDiaVencimento(lista.get(0).getDataVencimento().getDayOfMonth());
		return sd;
	}

	public void editaSaida(EditaSaidaDto payload) {
		Saida s = saidaRepo.findById(payload.getId()).get();
		List<Parcela> p = s.getListaParcelas();
		List<Classificacao> c = new ArrayList<>();
		List<Long> ids = payload.getTags();
		
		for(Long id : ids) {
			c.add(classificacaoRepo.findById(id).get());
		}
		
		for(Parcela par: p ) {
			par.setListaTags(c);
		}
		
		s.setListaParcelas(p);		
		
		s.setNome(payload.getNome());
		s.setObs(payload.getObs());
		saidaRepo.save(s);
	}

	public void deletarParcelas(DeletarParcelaRequest req) {
		if (req.getDeletarTudo()) {
			deletartudo(req.getIdParcela());
		}
		if (req.getDeletarRestante()) {
			deletarRestante(req.getIdParcela());
		} else {
			parcelaRepo.deleteById(req.getIdParcela());
		}
	}

	private void deletartudo(Long idParcela) {
		Parcela p = parcelaRepo.findById(idParcela).get();
		saidaRepo.delete(p.getSaida());
	}

	private void deletarRestante(Long idParcela) {
		Parcela p = parcelaRepo.findById(idParcela).get();
		List<Long> ids = p.getSaida().getListaParcelas()
				.stream()
				.filter(p1 -> p1.getSituacao().equals("Aberto"))
				.map(pp -> pp.getId())
				.collect(Collectors.toList());
		parcelaRepo.deleteAllByIdInBatch(ids);  // esse método foi o unico que deu certo para excluir o restante
	}

	public void atualziaParcelas(AtualizaParcelasRequest payload) {
		Saida s = saidaRepo.findById(payload.getIdSaida()).get();
		List<Parcela> parcel = s.getListaParcelas();
		List<Parcela> pay = payload.getParcelas();
		for(Parcela par: parcel) {
			par.setListaTags(payload.getTags());
			for(Parcela p: pay) {
				if(par.getId().equals(p.getId())) {
					par.setValor(p.getValor());;
				}
			}
		}
		s.setListaParcelas(parcel);
		saidaRepo.save(s);
	}
}
