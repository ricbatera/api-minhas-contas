package com.minhascontas.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minhascontas.domain.model.Parcela;

public interface ParcelaRepository extends JpaRepository<Parcela, Long>{

	List<Parcela> findByDataVencimentoBetween(LocalDate offsetDateTime, LocalDate offsetDateTime2);

}
