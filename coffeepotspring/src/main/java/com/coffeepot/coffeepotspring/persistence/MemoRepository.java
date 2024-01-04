package com.coffeepot.coffeepotspring.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coffeepot.coffeepotspring.model.MemoEntity;

public interface MemoRepository extends JpaRepository<MemoEntity, String> {
	
	List<MemoEntity> findByUserId(String userId);

}
