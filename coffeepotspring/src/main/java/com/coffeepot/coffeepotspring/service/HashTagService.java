package com.coffeepot.coffeepotspring.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.coffeepot.coffeepotspring.model.HashTagEntity;
import com.coffeepot.coffeepotspring.model.MemoEntity;
import com.coffeepot.coffeepotspring.persistence.HashTagRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashTagService {
	
	private final HashTagRepository hashTagRepository;
	
	public List<HashTagEntity> create(MemoEntity memoEntity, List<String> hashTags) {
		List<HashTagEntity> hashTagEntities = hashTags.stream().map(hashTag -> {
			return HashTagEntity.builder()
					.hashTag(hashTag)
					.memoEntity(memoEntity)
					.build();
		}).toList();
		return hashTagRepository.saveAll(hashTagEntities);
	}

	public List<HashTagEntity> retrieveByMemoEntity(MemoEntity memoEntity) {
		return hashTagRepository.findByMemoEntity(memoEntity);
	}
	
	public List<HashTagEntity> update(MemoEntity memoEntity, List<String> hashTags) {
		hashTagRepository.deleteByMemoEntity(memoEntity);
		return create(memoEntity, hashTags);
	}

}
