package com.minhascontas.domain.model;

import java.math.BigDecimal;
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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

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
	
	private String contagemParcelas;
	
//	@JsonManagedReference

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "conta_id")
	private ContaBancaria conta;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "faura_id")
	private Fatura fatura;
	
//	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "saida_id")
	private Saida saida;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name= "parcela_tag", joinColumns = @JoinColumn(name= "parcela_id"), inverseJoinColumns = @JoinColumn(name="tag_id"))
	private List<Classificacao> listaTags = new ArrayList<>();
	
	
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