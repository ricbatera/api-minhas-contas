package com.minhascontas.domain.repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minhascontas.domain.model.Fatura;

public interface FaturaRepository extends JpaRepository<Fatura, Long>{

	Optional<Fatura> findByDataVencimentoBetweenAndCartaoId(OffsetDateTime dataInicial,
			OffsetDateTime dataFinal, Long cartaoId);

	Fatura findByCartaoId(Long id);

	Fatura findByCartaoIdAndDataVencimento(Long id, LocalDate vencimento);

	List<Fatura> findByCartaoIdAndSituacao(Long id, Boolean situacao);

}
