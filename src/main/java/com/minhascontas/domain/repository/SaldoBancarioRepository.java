package com.minhascontas.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.minhascontas.domain.model.SaldoBancario;

public interface SaldoBancarioRepository extends JpaRepository<SaldoBancario, Long>{

	List<SaldoBancario> findByDataTransacaoLessThan(LocalDate data);
	
	//mesma acima - a query de cima fiz com keywords do spring data jpa
	@Query(value = "select * from saldo_bancario where data_transacao < ?",  nativeQuery = true)
	List<SaldoBancario> ateData(LocalDate data);

}
