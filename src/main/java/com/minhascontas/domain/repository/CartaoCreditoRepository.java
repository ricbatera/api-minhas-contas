package com.minhascontas.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minhascontas.domain.model.CartaoCredito;

public interface CartaoCreditoRepository extends JpaRepository<CartaoCredito, Long>{
	
	public List<CartaoCredito> findByStatus(Boolean status);

}
