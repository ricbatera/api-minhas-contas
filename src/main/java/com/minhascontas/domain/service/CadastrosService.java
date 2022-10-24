package com.minhascontas.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minhascontas.domain.mapper.CartaoCreditoMapper;
import com.minhascontas.domain.model.CartaoCredito;
import com.minhascontas.domain.repository.CartaoCreditoRepository;
import com.minhascontas.domain.request.CartaoCreditoRequest;

@Service
public class CadastrosService {

	@Autowired
	private CartaoCreditoRepository cartaoRepo;
	
	@Autowired
	private CartaoCreditoMapper cartaoMapper;
	
	public CartaoCredito novoCartaoCredito(CartaoCreditoRequest cartao) {
		CartaoCredito novoCartao = cartaoMapper.cartaoToModel(cartao);
		return cartaoRepo.save(novoCartao);
	}

}
