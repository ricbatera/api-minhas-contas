package com.minhascontas.domain.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;

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
public class Saida {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	private String nome;
	private String obs;
	
	@Column
	private LocalDate dataCompra;
	
	@JsonIgnore
	@Embedded
	private DadosTabela dados =new DadosTabela();
	
	private String meioPagto;
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "saida", cascade = CascadeType.ALL)
	private List<Parcela> listaParcelas = new ArrayList<>();
	
	@PrePersist
	public void setaEntradaSaidaNaLista() {
		listaParcelas.forEach(i -> i.setSaida(this));
	}

	@Override
	public String toString() {
		return "Saida [id=" + id + ", nome=" + nome + ", obs=" + obs + ", meioPagto=" + meioPagto + ", listaParcelas="
				+ listaParcelas + "]";
	}
	
	

}
