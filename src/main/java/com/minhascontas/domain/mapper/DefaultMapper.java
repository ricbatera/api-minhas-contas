package com.minhascontas.domain.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.minhascontas.domain.dto.CartaoCreditoDto;
import com.minhascontas.domain.dto.ContaBancariaDto;
import com.minhascontas.domain.dto.FaturaDto;
import com.minhascontas.domain.dto.ItemListaSaidaDto;
import com.minhascontas.domain.model.CartaoCredito;
import com.minhascontas.domain.model.ContaBancaria;
import com.minhascontas.domain.model.Fatura;
import com.minhascontas.domain.model.Parcela;
import com.minhascontas.domain.model.Saida;
import com.minhascontas.domain.request.CartaoCreditoRequest;
import com.minhascontas.domain.request.ContaBancariaRequest;
import com.minhascontas.domain.request.SaidaRequest;

@Component
public class DefaultMapper {
	
	private ModelMapper modelMapper = new ModelMapper();
	
	//CARTAO DE CREDITO
	public CartaoCredito cartaoToModel(CartaoCreditoRequest cartao) {
		return modelMapper.map(cartao, CartaoCredito.class);
	}

	public CartaoCreditoDto modelToDto (CartaoCredito cartao) {		
		return modelMapper.map(cartao, CartaoCreditoDto.class);
	}
	
	//CONTA BANCARIA
	public ContaBancaria contaBancariaRequestToModel(ContaBancariaRequest request) {
		return modelMapper.map(request, ContaBancaria.class);
	}
	
	public ContaBancariaDto contaBancariaModelToDto (ContaBancaria conta) {
		return modelMapper.map(conta, ContaBancariaDto.class);
	}
	
	//SAIDA
	public Saida saidaRequestToModel(SaidaRequest request) {
		return modelMapper.map(request, Saida.class);
	}
	
	public ItemListaSaidaDto modelSaidaToDto(Parcela parcela) {
		return modelMapper.map(parcela, ItemListaSaidaDto.class);
	}
	
	//FATURA
	public FaturaDto modelFaturaToDto (Fatura fatura) {
		return modelMapper.map(fatura, FaturaDto.class);
	}
}
