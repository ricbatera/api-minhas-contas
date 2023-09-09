package com.minhascontas.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;
import java.time.Month;
import java.time.format.TextStyle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minhascontas.core.utils.Utilitarios;
import com.minhascontas.domain.dto.CartaoCreditoDashboardDto;
import com.minhascontas.domain.dto.ClassificacaoGraficoDto;
import com.minhascontas.domain.dto.DashboardDto;
import com.minhascontas.domain.dto.DebitoBoletoDashboardDto;
import com.minhascontas.domain.dto.DevedortResponseDto;
import com.minhascontas.domain.dto.GraficoCategoriasPeriodoDto;
import com.minhascontas.domain.dto.SaldoDto;
import com.minhascontas.domain.mapper.DefaultMapper;
import com.minhascontas.domain.model.AnoGraficos;
import com.minhascontas.domain.model.ContaBancaria;
import com.minhascontas.domain.model.Devedor;
import com.minhascontas.domain.model.Fatura;
import com.minhascontas.domain.model.MesGraficos;
import com.minhascontas.domain.model.Parcela;
import com.minhascontas.domain.model.ParcelaEntrada;
import com.minhascontas.domain.model.SaldoBancario;
import com.minhascontas.domain.repository.ContaBancariaRepository;
import com.minhascontas.domain.repository.FaturaRepository;
import com.minhascontas.domain.repository.ParcelaEntradaRepository;
import com.minhascontas.domain.repository.ParcelaRepository;
import com.minhascontas.domain.repository.SaldoBancarioRepository;

@Service
public class DashboardService {

	
	@Autowired
	private SaldoBancarioRepository saldoBancarioRepo;
	
	@Autowired
	private FaturaRepository faturaRepo;

	
	@Autowired
	private ContaBancariaRepository contaRepo;
	
	@Autowired
	private ParcelaRepository parcelaRepo;
	
	@Autowired
	private ParcelaEntradaRepository parcelaEntradaRepo;
	
	@Autowired
	private DefaultMapper mapper;

	public DashboardDto getIndicadores(int mes, int ano) {
		
		// retornos do payload
		List<CartaoCreditoDashboardDto> faturasDoMes = new ArrayList<>();
		List<DevedortResponseDto> responseDevedores = new ArrayList<>();
		List<DebitoBoletoDashboardDto> boletosList = new ArrayList<>();
		List<ContaBancaria> contas = contaRepo.findByStatus(true);

		List<LocalDate> datasBase = Utilitarios.getDataInicialDataFinalLocalDateComAno(mes, ano);
		List<Parcela> parcelas = parcelaRepo.findByDataVencimentoBetween(datasBase.get(0), datasBase.get(1));
		List<Fatura> faturas = faturaRepo.findByDataVencimentoBetween(datasBase.get(0), datasBase.get(1));
		Set<Devedor> devedores = new HashSet<>();
		List<ParcelaEntrada> entradas = parcelaEntradaRepo.findByDataPrevistaRecebimentoBetween(datasBase.get(0), datasBase.get(1));
		
		//soma todas as saidas do mes
		BigDecimal totalSaidasDoMes = parcelas.stream()//retornar no payload
				.map(p -> p.getValor())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		BigDecimal totalEntradasDoMes = entradas.stream()
				.map(p-> p.getValor())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		BigDecimal totalPagoDoMes = parcelas.stream()
				.filter(p -> p.getValorPago() != null)
				.map(p -> p.getValor())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		BigDecimal totalRecebidoDoMes = entradas.stream()
				.filter(p -> p.getValorRecebido() != null)
				.map(p -> p.getValor())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		// pega as saídas boleto / débito
		List<Parcela> boletos = parcelas.stream()
				.filter(p-> p.getFatura() == null)
				.collect(Collectors.toList());
		for(Parcela p : boletos) {
			DebitoBoletoDashboardDto b = new DebitoBoletoDashboardDto();
			b.setId(p.getId());
			b.setSaida(p.getSaida());
			b.setValor(p.getValor());
			b.setVencimento(p.getDataPagamento());
			b.setSituacao(p.getSituacao());
			boletosList.add(b);
		}
		
		BigDecimal totalEmBoletos = boletosList.stream()
				.map(b -> b.getValor())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		// pega as entradas de cada devedor
		devedores = entradas.stream()
				.filter(p -> p.getDevedor() != null)
				.map(p -> p.getDevedor())
				.collect(Collectors.toSet());
		
		for(Devedor d: devedores) {
			List<ParcelaEntrada> ps = entradas.stream()
					.filter(p -> p.getDevedor() != null)
					.filter(p -> p.getDevedor().equals(d))
//					.collect(Collectors.toSet());	
					.collect(Collectors.toList());
			BigDecimal total = ps.stream()
					.map(b -> b.getValor())
					.reduce(BigDecimal.ZERO, BigDecimal::add);		
			DevedortResponseDto dev = new DevedortResponseDto();
			dev.setDevedor(d);
			dev.setParcelas(ps);
			dev.setTotal(total);
			responseDevedores.add(dev);
		}
		
		// pega somente as minhas saidas
		BigDecimal minhasSaidas = parcelas.stream()
				.filter(p -> p.getDevedor() == null)
				.map(b -> b.getValor())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		// pega somente as minhas entradas
				BigDecimal minhasEntradas = entradas.stream()
						.filter(p -> p.getDevedor() == null)
						.map(b -> b.getValor())
						.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		// fauras do mÊs
		for(Fatura f : faturas) {
			faturasDoMes.add(mapper.modelToCartaoCreditoDto(f));
			
		}
		
		DashboardDto response = new DashboardDto();
		response.setCartoes(faturasDoMes);
		response.setDebitoBoleto(boletosList);
		response.setDevedores(responseDevedores);
		response.setTotalEntradasDoMes(totalEntradasDoMes);
		response.setTotalPagoDoMes(totalPagoDoMes);
		response.setTotalRecebidoDoMes(totalRecebidoDoMes);
		response.setTotalSaidasDoMes(totalSaidasDoMes);
		response.setTotalEmBoletos(totalEmBoletos);
		response.setMinhasSaidas(minhasSaidas);
		response.setMinhasEntradas(minhasEntradas);
		response.setSaldo(getSaldosBancarios(mes, ano));
		response.setSaldoAcumulado(saldoPrevisto(mes, ano));
		response.setContas(contas);
		return response;
	}
	
	public SaldoDto getSaldosBancarios(int mes, int ano) {
		List<LocalDate> datasBase = Utilitarios.getDataInicialDataFinalLocalDateComAno(mes, ano);
		LocalDate data = datasBase.get(1);
		data = data.plusDays(1L);
		
		List<SaldoBancario> saldo = saldoBancarioRepo.findByDataTransacaoLessThan(data);

		
			BigDecimal totalEntradas = saldo.stream()
					.filter(el -> el.getTipo().equals("Entrada"))
					.map(el -> el.getValor())
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			
			BigDecimal totalSaidas = saldo.stream()
					.filter(el -> el.getTipo().equals("Saída"))
					.map(el -> el.getValor())
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			
			return new SaldoDto(totalSaidas, totalEntradas, totalEntradas.subtract(totalSaidas));		
	
	}
	
	public BigDecimal saldoPrevisto(int mes, int ano) {
		
		List<LocalDate> datasBase = Utilitarios.getDataInicialDataFinalLocalDateComAno(mes, ano);
		LocalDate data = datasBase.get(1);
		data = data.plusDays(1L);
		
		List <Parcela> parcelas = parcelaRepo.findByDataVencimentoLessThan(data);
		List <ParcelaEntrada> entradas = parcelaEntradaRepo.findByDataPrevistaRecebimentoLessThan(data);
		
		BigDecimal totalEntradas = entradas.stream()
				.filter(p -> p.getDevedor() == null)
				.map(e-> e.getValor())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		BigDecimal totalSaidas = parcelas.stream()
				.filter(p -> p.getDevedor() == null)
				.map(e-> e.getValor())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		
		
		return totalEntradas.subtract(totalSaidas);
	}

	public GraficoCategoriasPeriodoDto getGraficos(int mesIn, int anoIn, int mesOut, int anoOut) {
		GraficoCategoriasPeriodoDto result = new GraficoCategoriasPeriodoDto();
		LocalDate dataInicial = Utilitarios.getDataInicialDataFinalLocalDateComAno(mesIn, anoIn).get(0);
		LocalDate dataFinal = Utilitarios.getDataInicialDataFinalLocalDateComAno(mesOut, anoOut).get(1);
		List<Parcela> parcelas = parcelaRepo.findByDataVencimentoBetween(dataInicial, dataFinal);
		
		result.setCategorias(parcelas.stream()
				.filter(p-> p.getClassificacao() !=null)
				.map(p-> p.getClassificacao().getNome())
				.collect(Collectors.toSet()));
		
		Set<String> categorias = result.getCategorias();
		List<BigDecimal> valores = new ArrayList<>();
		for(String cat: categorias) {
			BigDecimal total = parcelas.stream()
					.filter(p-> p.getClassificacao().getNome().equals(cat))
					.map(p-> p.getValor())
					.reduce(BigDecimal.ZERO, BigDecimal::add);
			valores.add(total);
		}
		
		result.setValores(valores);
		System.out.println(categorias);
		
		/*  
		 * 
		 * 
		 * devedores = entradas.stream()
				.filter(p -> p.getDevedor() != null)
				.map(p -> p.getDevedor())
				.collect(Collectors.toSet());*/
		
		
		
		return result;
	}

	public List<ClassificacaoGraficoDto> graficoGeralMes(int mesStart, int anoStart, int mesEnd, int anoEnd) {		
		LocalDate dataInicial = Utilitarios.getDataInicialDataFinalLocalDateComAno(mesStart, anoStart).get(0);
		LocalDate dataFinal = Utilitarios.getDataInicialDataFinalLocalDateComAno(mesEnd, anoEnd).get(1);
		
		//buscando na base
		List<Parcela> parcelas = parcelaRepo.findByDataVencimentoBetween(dataInicial, dataFinal);
		
		// criando a lista de retorno vazia
		List<ClassificacaoGraficoDto> resultTagList = new ArrayList<>();
		
		for (Parcela parcela : parcelas) {
		    boolean found = false;

		    for (ClassificacaoGraficoDto tag : resultTagList) {
		        if (tag.getNome().equals(parcela.getClassificacao().getNome())) {
		            BigDecimal v = tag.getValor();
		            tag.setValor(tag.getValor().add(parcela.getValor()));
		            found = true;
		            break;
		        }
		    }

		    if (!found) {
		        resultTagList.add(new ClassificacaoGraficoDto(parcela.getClassificacao().getNome(), parcela.getValor()));
		    }
		}	
		
		return resultTagList;
	}

	public List<AnoGraficos> graficoGeral(int mesStart, int anoStart, int mesEnd, int anoEnd, Long idDevedor) {
		LocalDate dataInicial = Utilitarios.getDataInicialDataFinalLocalDateComAno(mesStart, anoStart).get(0);
		LocalDate dataFinal = Utilitarios.getDataInicialDataFinalLocalDateComAno(mesEnd, anoEnd).get(1);
		List<Parcela> parcelas = new ArrayList<>();
		if(idDevedor == 0) {
			parcelas = parcelaRepo.findByDataVencimentoBetweenAndDevedorIdIsNull(dataInicial, dataFinal);
		}else {
			parcelas = parcelaRepo.findByDataVencimentoBetweenAndDevedorId(dataInicial, dataFinal, idDevedor);
		}
		
		List<AnoGraficos> result = new ArrayList<>();
		 for(Parcela item: parcelas) {
			 Locale locale = new Locale("pt", "BR");
			 int ano = item.getDataVencimento().getYear();
			 Month mes = item.getDataVencimento().getMonth();
	         BigDecimal valor = item.getValor();
	         boolean found = false;
	         
	         for(AnoGraficos itemAno: result) {
	        	 if(itemAno.getAno() == ano) {
	        		 found = true;
	        		 List<MesGraficos> l = itemAno.getMeses();
	        		 boolean notMes = false;
	        		 for(MesGraficos m : l) {
	        			 if(m.getMes().equals(mes)) {
	        				 m.setValor(m.getValor().add(valor));
	        				 notMes = true;
	        			 }
	        		 }
	        		 if(!notMes) {
	        			 l.add(novoMes(
	        			 			mes.getDisplayName(TextStyle.FULL, locale),
	        			 			mes.getDisplayName(TextStyle.SHORT, locale),
	        			 			mes.getValue(),
	        			 			valor,
	        			 			mes
	        			 			));
	        		 }
	        	 }
	         }
	         
	         if(!found) {
	        	 List<MesGraficos> meses = new ArrayList<>();
	        	 meses.add(novoMes(
	        			 			mes.getDisplayName(TextStyle.FULL, locale),
	        			 			mes.getDisplayName(TextStyle.SHORT, locale),
	        			 			mes.getValue(),
	        			 			valor,
	        			 			mes
	        			 			));
	        	 result.add(new AnoGraficos(ano, meses));
	         }
		 }
		 return result;		
	}
	
	private MesGraficos novoMes(String nomeMes, String nomeMesAbreviado, int mesNumero, BigDecimal valor, Month mes) {
		return new MesGraficos(nomeMes, nomeMesAbreviado, mesNumero, valor, mes); 
	}

}
