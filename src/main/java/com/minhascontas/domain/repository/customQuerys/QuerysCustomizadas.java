package com.minhascontas.domain.repository.customQuerys;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.minhascontas.domain.model.Parcela;

@Repository
public class QuerysCustomizadas {
	@PersistenceContext
	private EntityManager em;
	
	public List<Parcela> findByFilterTag(){
		StringBuilder query = new StringBuilder("from Parcela p where p.tag.get");
		return null;
	}
	

}
