package com.minhascontas.domain.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.minhascontas.domain.dto.CartaoCreditoDashboardDto;
import com.minhascontas.domain.dto.CartaoCreditoDto;
import com.minhascontas.domain.dto.ClassificacaoDto;
import com.minhascontas.domain.dto.ContaBancariaDto;
import com.minhascontas.domain.dto.DevedorDto;
import com.minhascontas.domain.dto.FaturaDto;
import com.minhascontas.domain.dto.ItemListaEntradaDto;
import com.minhascontas.domain.dto.ItemListaSaidaDto;
import com.minhascontas.domain.dto.ParcelaDto;
import com.minhascontas.domain.dto.SaidaDto;
import com.minhascontas.domain.model.CartaoCredito;
import com.minhascontas.domain.model.Classificacao;
import com.minhascontas.domain.model.ContaBancaria;
import com.minhascontas.domain.model.Devedor;
import com.minhascontas.domain.model.Entrada;
import com.minhascontas.domain.model.Fatura;
import com.minhascontas.domain.model.Parcela;
import com.minhascontas.domain.model.ParcelaEntrada;
import com.minhascontas.domain.model.Saida;
import com.minhascontas.domain.request.CartaoCreditoRequest;
import com.minhascontas.domain.request.ClassificacaoRequest;
import com.minhascontas.domain.request.ContaBancariaRequest;
import com.minhascontas.domain.request.DevedorRequest;
import com.minhascontas.domain.request.EntradaRequest;
import com.minhascontas.domain.request.SaidaRequest;

@Component
public class DefaultMapper {

	private ModelMapper modelMapper = new ModelMapper();

	// CARTAO DE CREDITO
	public CartaoCredito cartaoToModel(CartaoCreditoRequest cartao) {
		return modelMapper.map(cartao, CartaoCredito.class);
	}

	public CartaoCreditoDto modelToDto(CartaoCredito cartao) {
		return modelMapper.map(cartao, CartaoCreditoDto.class);
	}

	// CONTA BANCARIA
	public ContaBancaria contaBancariaRequestToModel(ContaBancariaRequest request) {
		return modelMapper.map(request, ContaBancaria.class);
	}

	public ContaBancariaDto contaBancariaModelToDto(ContaBancaria conta) {
		return modelMapper.map(conta, ContaBancariaDto.class);
	}

	// SAIDA
	public Saida saidaRequestToModel(SaidaRequest request) {
		return modelMapper.map(request, Saida.class);
	}

	public ItemListaSaidaDto modelSaidaToDto(Parcela parcela) {
		return modelMapper.map(parcela, ItemListaSaidaDto.class);
	}
	
	public SaidaDto modelToSaidaDto(Saida saida) {
		return modelMapper.map(saida, SaidaDto.class);
	}
	
	public ParcelaDto modelToParcelaDto(Parcela p) {
		return modelMapper.map(p, ParcelaDto.class);
	}

	// FATURA
	public FaturaDto modelFaturaToDto(Fatura fatura) {
		return modelMapper.map(fatura, FaturaDto.class);
	}
	
	public CartaoCreditoDashboardDto modelToCartaoCreditoDto (Fatura fatura) {
		return modelMapper.map(fatura, CartaoCreditoDashboardDto.class);
	}

	// ENTRADA
	public Entrada requestEntradaToModel(EntradaRequest payload) {
		return modelMapper.map(payload, Entrada.class);
	}

	public ItemListaEntradaDto modelToItemListaEntradaDto(ParcelaEntrada parcela) {
		return modelMapper.map(parcela, ItemListaEntradaDto.class);
	}

	// DEVEDOR
	public Devedor requestDevedorToModel(DevedorRequest payload) {
		return modelMapper.map(payload, Devedor.class);
	}

	public DevedorDto modelToDevedorDto(Devedor devedor) {
		return modelMapper.map(devedor, DevedorDto.class);
	}

	// CLASSIFICAÇÃO
	public Classificacao requestClassificacaoToModel(ClassificacaoRequest payload) {
		return modelMapper.map(payload, Classificacao.class);
	}

	public ClassificacaoDto modelToClassificacaoDto(Classificacao classificacao) {
		return modelMapper.map(classificacao, ClassificacaoDto.class);
	}

}
