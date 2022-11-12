package com.minhascontas.domain.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minhascontas.core.utils.Utilitarios;
import com.minhascontas.domain.dto.CartaoCreditoDashboardDto;
import com.minhascontas.domain.dto.DashboardDto;
import com.minhascontas.domain.dto.DebitoBoletoDashboardDto;
import com.minhascontas.domain.dto.DevedorDto;
import com.minhascontas.domain.dto.DevedortResponseDto;
import com.minhascontas.domain.dto.FaturaDto;
import com.minhascontas.domain.mapper.DefaultMapper;
import com.minhascontas.domain.model.Devedor;
import com.minhascontas.domain.model.Fatura;
import com.minhascontas.domain.model.Parcela;
import com.minhascontas.domain.model.ParcelaEntrada;
import com.minhascontas.domain.repository.CartaoCreditoRepository;
import com.minhascontas.domain.repository.ContaBancariaRepository;
import com.minhascontas.domain.repository.FaturaRepository;
import com.minhascontas.domain.repository.ParcelaEntradaRepository;
import com.minhascontas.domain.repository.ParcelaRepository;
import com.minhascontas.domain.repository.SaidaRepository;

@Service
public class DashboardService {

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
	private ParcelaEntradaRepository parcelaEntradaRepo;
	
	@Autowired
	private DefaultMapper mapper;

	public DashboardDto getIndicadores(int mes) {
		
		// retornos do payload
		List<CartaoCreditoDashboardDto> faturasDoMes = new ArrayList<>();
		List<DevedortResponseDto> responseDevedores = new ArrayList<>();
		List<DebitoBoletoDashboardDto> boletosList = new ArrayList<>();

		List<LocalDate> datasBase = Utilitarios.getDataInicialDataFinalLocalDate(mes);
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
		return response;
	}
	
	

}
