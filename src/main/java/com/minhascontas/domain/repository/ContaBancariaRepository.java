package com.minhascontas.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minhascontas.domain.model.ContaBancaria;

public interface ContaBancariaRepository extends JpaRepository<ContaBancaria, Long>{

	List<ContaBancaria> findByStatus(boolean b);

}
