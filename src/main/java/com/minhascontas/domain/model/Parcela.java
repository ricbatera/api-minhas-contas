package com.minhascontas.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
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
//	@JsonBackReference
//	@ManyToOne
//	@JoinColumn(name = "saida_id")
//	private Saida saida;	
	
	@ManyToOne
	@JoinColumn(name = "cartao_id")
	private CartaoCredito cartao;
	
	@ManyToOne
	@JoinColumn(name = "faura_id")
	private Fatura fatura;
	
	private String situacao = "Aberto";


	@Override
	public String toString() {
		return "Parcela [id=" + id + ", dataVencimento=" + dataVencimento + ", dataPagamento=" + dataPagamento
				+ ", valorEsperado=" + valor + ", valorEfetivo=" + valorPago
				+ ", status=" + situacao + "]";
	}
	
	

}