package com.coffeepot.coffeepotspring.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coffeepot.coffeepotspring.model.HashTagEntity;
import com.coffeepot.coffeepotspring.model.MemoEntity;

public interface HashTagRepository extends JpaRepository<HashTagEntity, String> {

	List<HashTagEntity> findByMemoEntity(MemoEntity memoEntity);
	
	void deleteByMemoEntity(MemoEntity memoEntity);

}
