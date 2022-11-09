package com.minhascontas.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minhascontas.domain.model.ParcelaEntrada;

public interface ParcelaEntradaRepository extends JpaRepository<ParcelaEntrada, Long>{

	List<ParcelaEntrada> findByDataPrevistaRecebimentoBetween(LocalDate localDate, LocalDate localDate2);

}
