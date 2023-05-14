package com.minhascontas.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.minhascontas.domain.model.Tag;
import com.minhascontas.domain.service.RecursosService;

@CrossOrigin
@RestController
@RequestMapping("/recursos")
public class RecursosController {
	
	@Autowired
	private RecursosService recursoService;
		
	@PostMapping("/nova-tag")
	@ResponseStatus(code = HttpStatus.CREATED)
	public void novaTag(@RequestBody Tag tag) {
		recursoService.novaTag(tag);
	}
	
	@GetMapping("/listar-todas/tags")
	public List<Tag> getAllTags(){
		return recursoService.getAllTags();
	}
}
