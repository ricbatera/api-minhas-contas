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
import javax.persistence.ManyToOne;
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
public class Fatura {
	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private BigDecimal valor;
	@Column
	private BigDecimal valorPago;
	private Boolean situacao= true;
	@Column
	private LocalDate dataVencimento;
	@Column
	private LocalDate dataPagamento;
	
	@ManyToOne
	@JoinColumn(name = "cartao_id")
	private CartaoCredito cartao;
	
	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "fatura", cascade = CascadeType.ALL)
	private List<Parcela> itensFatura = new ArrayList<>();
	
	@ManyToOne
	@JoinColumn(name = "conta_id")
	private ContaBancaria conta;
	
	@JsonIgnore
	@Embedded
	private DadosTabela dados =new DadosTabela();
	
	@PrePersist
	public void setaEntradaSaidaNaLista() {
		itensFatura.forEach(i -> i.setFatura(this));
	}

	@Override
	public String toString() {
		return "Fatura [dataVencimento=" + dataVencimento +  " Valor Total: " + valor + " ID: "+ id + " PARCELAS: "+ itensFatura +"]";
	}
	
	

}
