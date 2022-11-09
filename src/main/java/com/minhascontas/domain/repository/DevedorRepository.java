package com.minhascontas.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minhascontas.domain.model.Devedor;

public interface DevedorRepository extends JpaRepository<Devedor, Long>{

}
