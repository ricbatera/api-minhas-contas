package com.minhascontas.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter()
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ParcelaEntrada {
	
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column
	private LocalDate dataPrevistaRecebimento;
	@Column
	private LocalDate dataRecebimento;
	@Column
	private BigDecimal valor;
	@Column
	private BigDecimal valorRecebido;
	
//	@JsonManagedReference

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "conta_id")
	private ContaBancaria conta;
	
		
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "entrada_id")
	private Entrada entrada;
	
	private String situacao = "Aberto";
	
	@JsonIgnore
	@Embedded
	private DadosTabela dados =new DadosTabela();

	@Override
	public String toString() {
		return "Parcela [id=" + id + ", dataVencimento=" + dataPrevistaRecebimento + ", dataRecebimento=" + dataRecebimento
				+ ", valorEsperado=" + valor + ", valorEfetivo=" + valorRecebido
				+ ", status=" + situacao + "]";
	}
	
	

}