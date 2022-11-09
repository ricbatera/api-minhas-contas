package com.minhascontas.domain.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minhascontas.core.utils.Utilitarios;
import com.minhascontas.domain.dto.CartaoCreditoDashboardDto;
import com.minhascontas.domain.dto.DashboardDto;
import com.minhascontas.domain.mapper.DefaultMapper;
import com.minhascontas.domain.model.Fatura;
import com.minhascontas.domain.model.Parcela;
import com.minhascontas.domain.repository.CartaoCreditoRepository;
import com.minhascontas.domain.repository.ContaBancariaRepository;
import com.minhascontas.domain.repository.FaturaRepository;
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
	private DefaultMapper mapper;

	public List<DashboardDto> getIndicadores(int mes) {
		List<LocalDate> datasBase = Utilitarios.getDataInicialDataFinalLocalDate(mes);
		List<Parcela> parcelas = parcelaRepo.findByDataVencimentoBetween(datasBase.get(0), datasBase.get(1));
		List<CartaoCreditoDashboardDto> faturas = new ArrayList<>();
		
		parcelas.forEach(p->{
			if(p.getFatura() !=null) {
				CartaoCreditoDashboardDto d = new CartaoCreditoDashboardDto();
				d.setNome(p.getFatura().getCartao().getNome());
				d.setDescricao(p.getFatura().getCartao().getDescricao());
				d.setIdFatura(p.getFatura().getId());
				d.setValorFatura(p.getFatura().getValor());
				d.setVencimento(p.getDataVencimento());
				faturas.add(d);
			}
		});
		
		faturas.forEach(System.out::println);
		return null;
	}
	
	

}
