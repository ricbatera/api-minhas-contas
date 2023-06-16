package com.minhascontas.domain.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class Classificacao {
	
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	
	private String nome;
	
	private String tipo;
	
	private Boolean status = true;
	
	@JsonIgnore
	@JsonBackReference
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "listaTags", cascade = CascadeType.ALL)
	private List<Parcela> parcelas;
	
	@JsonIgnore
	@Embedded
	private DadosTabela dados =new DadosTabela();

}
