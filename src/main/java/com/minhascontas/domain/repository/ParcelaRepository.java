package com.minhascontas.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minhascontas.domain.model.Parcela;
import com.minhascontas.domain.model.Tag;

public interface ParcelaRepository extends JpaRepository<Parcela, Long>{

	List<Parcela> findByDataVencimentoBetween(LocalDate offsetDateTime, LocalDate offsetDateTime2);
	
	List<Parcela> findByDataVencimentoBetweenAndDevedorIdIsNull(LocalDate offsetDateTime, LocalDate offsetDateTime2);
	
	List<Parcela> findByDataVencimentoBetweenAndDevedorId(LocalDate offsetDateTime, LocalDate offsetDateTime2, Long devedorId);
	
	//List<Parcela> findByDataVencimentoBetweenAndListaTags(LocalDate offsetDateTime, LocalDate offsetDateTime2, List<Tag> tags);

	List<Parcela> findBySaidaId(Long id);

	List<Parcela> findByDataVencimentoLessThan(LocalDate data);

}
