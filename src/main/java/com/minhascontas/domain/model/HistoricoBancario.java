package com.minhascontas.domain.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HistoricoBancario {
	
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@JsonIgnore
	@Embedded
	private DadosTabela dados =new DadosTabela();	
	
//	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "conta_id")
	private ContaBancaria conta;
		
	@Column
	private int mes;
	
	@Column
	private int ano;
	
	@Column
	private BigDecimal valor;

}
