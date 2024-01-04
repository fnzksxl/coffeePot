package com.coffeepot.coffeepotspring.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.coffeepot.coffeepotspring.model.MemoEntity;
import com.coffeepot.coffeepotspring.persistence.MemoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemoService {
	
	private final MemoRepository memoRepository;
	
	private void validate(final MemoEntity memoEntity) {
		if (memoEntity == null) {
			log.warn("Entity cannot be null");
			throw new RuntimeException("Entity cannot be null");
		}
		if (memoEntity.getUserId() == null) {
			log.warn("Unknown user");
			throw new RuntimeException("Unknown User");
		}
	}
	
	public List<MemoEntity> create(final MemoEntity memoEntity) {
		validate(memoEntity);
		
		if (memoEntity == null || memoEntity.getContent() == null) {
			throw new RuntimeException("Invalid arguments");
		}
		memoRepository.save(memoEntity);
		
		return memoRepository.findByUserId(memoEntity.getUserId());
	}
	
	public List<MemoEntity> retrieveByUserId(final String userId) {
		if (userId == null) {
			throw new RuntimeException("Invalid arguments");
		}
		return memoRepository.findByUserId(userId);
	}
	
	public List<MemoEntity> update(final MemoEntity memoEntity) {
		validate(memoEntity);
		
		final Optional<MemoEntity> original = memoRepository.findById(memoEntity.getId());
		original.ifPresent(memo -> {
			memo.setTitle(memoEntity.getTitle());
			memo.setContent(memoEntity.getContent());
			memo.setVisibility(memoEntity.getVisibility());
			memoRepository.save(memo);
		});
		
		return retrieveByUserId(memoEntity.getUserId());
	}
	
	public List<MemoEntity> delete(final MemoEntity memoEntity) {
		validate(memoEntity);
		
		try {
			memoRepository.delete(memoEntity);
		} catch (Exception e) {
			log.error("Error deleting MemoEntity");
			throw new RuntimeException("Error deleting MemoEntity" + memoEntity.getId());
		}
		
		return retrieveByUserId(memoEntity.getUserId());
	}

}
