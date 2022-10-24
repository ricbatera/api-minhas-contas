package com.minhascontas.domain.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

@Embeddable
public class DadosTabela {
	
//	@CreationTimestamp
//	@Temporal(TemporalType.TIMESTAMP)
	@Column(columnDefinition = "datetime(2)")
	private LocalDateTime dataCriacao;
	
//	@UpdateTimestamp
//	@Temporal(TemporalType.TIMESTAMP)
	@Column(columnDefinition = "datetime(2)")
	private LocalDateTime dataAlteracao;
	
	@PrePersist
    private void setaDatas() {
		dataCriacao = LocalDateTime.now();
		dataAlteracao = LocalDateTime.now();
		System.out.println("Stenado data " + dataCriacao.toString());
    }
 
    @PreUpdate
    public void preUpdate() {
    	dataAlteracao = LocalDateTime.now();
    	System.out.println("Stenado data " + dataCriacao.toString());
    } 
	
}
