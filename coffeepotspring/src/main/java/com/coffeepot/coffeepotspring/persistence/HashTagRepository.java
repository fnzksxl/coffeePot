package com.coffeepot.coffeepotspring.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coffeepot.coffeepotspring.model.HashTagEntity;

public interface HashTagRepository extends JpaRepository<HashTagEntity, String> {

}
