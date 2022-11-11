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
public class Parcela {
	
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column
	private LocalDate dataVencimento;
	@Column
	private LocalDate dataPagamento;
	@Column
	private BigDecimal valor;
	@Column
	private BigDecimal valorPago;
	
//	@JsonManagedReference

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "conta_id")
	private ContaBancaria conta;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "faura_id")
	private Fatura fatura;
	
	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "saida_id")
	private Saida saida;
	
	
	@ManyToOne
	@JoinColumn(name = "classificacao_id")
	private Classificacao classificacao;
	
	
	@ManyToOne
	@JoinColumn(name = "devedor_id")
	private Devedor devedor;
	
	private String situacao = "Aberto";
	
	@JsonIgnore
	@Embedded
	private DadosTabela dados =new DadosTabela();

	@Override
	public String toString() {
		return "Parcela [id=" + id + ", dataVencimento=" + dataVencimento + ", dataPagamento=" + dataPagamento
				+ ", valorEsperado=" + valor + ", valorEfetivo=" + valorPago
				+ ", status=" + situacao + "]";
	}
	
	

}