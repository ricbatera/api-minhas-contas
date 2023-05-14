package com.minhascontas.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.minhascontas.domain.model.Tag;
import com.minhascontas.domain.repository.TagRepository;

@Service
public class RecursosService {
	
	@Autowired
	private TagRepository tagRepo;
	
	public void novaTag(Tag tag) {
		tagRepo.save(tag);
	}
	
	public List<Tag> getAllTags(){
		return tagRepo.findAll();
	}

}
