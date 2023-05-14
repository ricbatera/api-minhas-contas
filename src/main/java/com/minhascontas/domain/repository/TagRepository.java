package com.minhascontas.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.minhascontas.domain.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Long>{

}
