package com.coffeepot.coffeepotspring.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coffeepot.coffeepotspring.model.ImageDataEntity;
import com.coffeepot.coffeepotspring.model.MemoEntity;

public interface ImageRepository extends JpaRepository<ImageDataEntity, String> {
	
	public List<ImageDataEntity> findAllByMemoEntity(MemoEntity memoEntity);
	
	public void deleteAllBySavedName(String SavedName);

}
