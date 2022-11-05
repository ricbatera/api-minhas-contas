package com.minhascontas.core.exceptions;

import java.time.OffsetDateTime;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class HttpErrosException extends ResponseEntityExceptionHandler{
	
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		
		if(body == null) {
			body = Problema.builder()
					.data(OffsetDateTime.now())
					.mensagem("Erro Inesperado")
					.mensagemSistema(ex.getMessage()).build();			
		} else if(body instanceof String ) {
			body = Problema.builder()
					.data(OffsetDateTime.now())
					.mensagem((String) body)
					.mensagemSistema(ex.getMessage()).build();	
		}
		
		return super.handleExceptionInternal(ex, body, headers, status, request);
	}
	
	// tratai aqui as execeções de métodos não habilitados na API
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		Problema body = Problema.builder()
				.mensagem("Método HTTP não suportado, verifique o método usado, e se o erro persistir, contate do administrador do sistema")
				.data(OffsetDateTime.now())
				.mensagemSistema(status.getReasonPhrase())
				.build();
		return super.handleExceptionInternal(ex, body, headers, status, request);
	}
	
	//Tratei aqui os endpoints que não existem na API
	// não funcionou hoje 24/10/2022, pois essa excessão não é lançada e não tem log 
	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {

		Problema body = Problema.builder()
				.mensagem("Esse endpoint não existe, verifique a documentação ou contate do administrador do sistema")
				.data(OffsetDateTime.now())
				.mensagemSistema(status.getReasonPhrase())
				.build();
		return super.handleExceptionInternal(ex, body, headers, status, request);
	}
	
	public static ResponseEntity<Object> registroNaoEncontradoException(){
		return ResponseEntity.notFound().build();
	}

}
