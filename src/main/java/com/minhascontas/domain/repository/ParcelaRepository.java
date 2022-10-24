package com.minhascontas.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minhascontas.domain.model.CartaoCredito;
import com.minhascontas.domain.model.Parcela;

public interface ParcelaRepository extends JpaRepository<Parcela, Long>{

}
