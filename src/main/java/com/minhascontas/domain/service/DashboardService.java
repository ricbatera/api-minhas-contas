package com.minhascontas.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minhascontas.core.utils.Utilitarios;
import com.minhascontas.domain.dto.CartaoCreditoDashboardDto;
import com.minhascontas.domain.dto.DashboardDto;
import com.minhascontas.domain.dto.DebitoBoletoDashboardDto;
import com.minhascontas.domain.dto.DevedortResponseDto;
import com.minhascontas.domain.dto.SaldoDto;
import com.minhascontas.domain.mapper.DefaultMapper;
import com.minhascontas.domain.model.ContaBancaria;
import com.minhascontas.domain.model.Devedor;
import com.minhascontas.domain.model.Fatura;
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

}
