package com.minhascontas.domain.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.minhascontas.domain.model.CartaoCredito;
import com.minhascontas.domain.request.CartaoCreditoRequest;

@Component
public class CartaoCreditoMapper {
	
	private ModelMapper modelMapper = new ModelMapper();
	
	public CartaoCredito cartaoToModel(CartaoCreditoRequest cartao) {
		return modelMapper.map(cartao, CartaoCredito.class);
	}

}
