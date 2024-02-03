package com.coffeepot.coffeepotspring.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffeepot.coffeepotspring.model.MemoEntity;
import com.coffeepot.coffeepotspring.model.UserEntity;
import com.coffeepot.coffeepotspring.model.UserLikeMemo;
import com.coffeepot.coffeepotspring.model.UserScrapMemo;
import com.coffeepot.coffeepotspring.persistence.MemoRepository;
import com.coffeepot.coffeepotspring.persistence.UserLikeMemoRepository;
import com.coffeepot.coffeepotspring.persistence.UserRepository;
import com.coffeepot.coffeepotspring.persistence.UserScrapMemoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemoService {
	
	private final MemoRepository memoRepository;
	private final UserRepository userRepository;
	private final UserLikeMemoRepository userLikeMemoRepository;
	private final UserScrapMemoRepository userScrapMemoRepository;
	
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
	
	public MemoEntity create(final MemoEntity memoEntity) {
		validate(memoEntity);
		
		if (memoEntity == null || memoEntity.getContent() == null) {
			throw new RuntimeException("Invalid arguments");
		}
		return memoRepository.save(memoEntity);
	}
	
	public List<MemoEntity> retrieveAll() {
		return memoRepository.findAll();
	}
	
	public Page<MemoEntity> retrieveAll(String memoId, int pageSize, String sortBy) {
		Pageable pageable = PageRequest.of(0, pageSize, Sort.by(sortBy).descending());
		if ("".equals(memoId)) {
			Optional<MemoEntity> oMemoEntity = memoRepository.findFirstByOrderByCreatedAtDesc();
			MemoEntity memoEntity = oMemoEntity.get();
			return memoRepository.findAllByCreatedAtLessThanEqualOrderByCreatedAtDesc(memoEntity.getCreatedAt(), pageable);
		} else {
			Optional<MemoEntity> oMemoEntity = memoRepository.findById(memoId);
			MemoEntity memoEntity = oMemoEntity.get();
			return memoRepository.findAllByCreatedAtLessThanOrderByCreatedAtDesc(memoEntity.getCreatedAt(), pageable);
		}
	}
	
	// 자식 엔티티를 호출할 경우 메소드에 @Transactional을 추가해야 함
	public List<MemoEntity> retrieveByUserId(final String userId) {
		if (userId == null) {
			throw new RuntimeException("Invalid arguments");
		}
		return memoRepository.findByUserId(userId);
	}

	public Page<MemoEntity> retrieveByUserId(String userId, String memoId, int pageSize, String sortBy) {
		Pageable pageable = PageRequest.of(0, pageSize, Sort.by(sortBy).descending());
		if ("".equals(memoId)) {
			Optional<MemoEntity> oMemoEntity = memoRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
			MemoEntity memoEntity = oMemoEntity.get();
			return memoRepository.findAllByUserIdAndCreatedAtLessThanEqualOrderByCreatedAtDesc(userId, memoEntity.getCreatedAt(), pageable);
		} else {
			Optional<MemoEntity> oMemoEntity = memoRepository.findById(memoId);
			MemoEntity memoEntity = oMemoEntity.get();
			return memoRepository.findAllByUserIdAndCreatedAtLessThanEqualOrderByCreatedAtDesc(userId, memoEntity.getCreatedAt(), pageable);
		}
	}
	
	public MemoEntity retrieveById(final String memoId) {
		return memoRepository.findById(memoId).get();
	}
	
	public MemoEntity update(final MemoEntity memoEntity) {
		validate(memoEntity);
		
		final Optional<MemoEntity> original = memoRepository.findById(memoEntity.getId());
		
		original.ifPresent(memo -> {
			memo.setTitle(memoEntity.getTitle());
			memo.setContent(memoEntity.getContent());
			memo.setVisibility(memoEntity.getVisibility());
			memo.setUpdatedAt(LocalDateTime.now());
			memoRepository.save(memo);
		});
		
		return memoRepository.findById(original.get().getId()).get();
	}
	
	public List<MemoEntity> delete(final MemoEntity memoEntity) {
		validate(memoEntity);
		
		try {
			MemoEntity memoEntityToBeDeleted = memoRepository.findById(memoEntity.getId()).get();
			memoRepository.delete(memoEntityToBeDeleted);
		} catch (Exception e) {
			log.error("Error deleting MemoEntity");
			throw new RuntimeException("Error deleting MemoEntity" + memoEntity.getId());
		}
		
		return retrieveByUserId(memoEntity.getUserId());
	}
	
	public void like(final String userId, final String memoId) {
		Optional<UserEntity> oUserEntity = userRepository.findById(userId);
		Optional<MemoEntity> oMemoEntity = memoRepository.findById(memoId);
		
		if (oUserEntity.isPresent() && oMemoEntity.isPresent()) {
			UserEntity userEntity = oUserEntity.get();
			MemoEntity memoEntity = oMemoEntity.get();
			
			Optional<UserLikeMemo> oUserLikeMemo = userLikeMemoRepository.findByMemoLikerAndMemoEntity(userEntity, memoEntity);
			oUserLikeMemo.ifPresentOrElse(userLikeMemo -> {
				throw new RuntimeException("User already liked memo");
			}, () -> {
				UserLikeMemo userLikeMemo = UserLikeMemo.builder()
						.memoLiker(userEntity)
						.memoEntity(memoEntity)
						.build();
				userLikeMemoRepository.save(userLikeMemo);
				memoEntity.setLikeCount(memoEntity.getLikeCount() + 1);
				memoRepository.save(memoEntity);
			});
		}
	}
	
	public void scrap(final String userId, final String memoId) {
		Optional<UserEntity> oUserEntity = userRepository.findById(userId);
		Optional<MemoEntity> oMemoEntity = memoRepository.findById(memoId);
		
		if (oUserEntity.isPresent() && oMemoEntity.isPresent()) {
			UserEntity userEntity = oUserEntity.get();
			MemoEntity memoEntity = oMemoEntity.get();
			
			Optional<UserScrapMemo> oUserScrapMemo = userScrapMemoRepository.findByMemoScraperAndMemoEntity(userEntity, memoEntity);
			oUserScrapMemo.ifPresentOrElse(userLikeMemo -> {
				throw new RuntimeException("User already scraped memo");
			}, () -> {
				UserScrapMemo userScrapMemo = UserScrapMemo.builder()
						.memoScraper(userEntity)
						.memoEntity(memoEntity)
						.build();
				userScrapMemoRepository.save(userScrapMemo);
				memoEntity.setScrapCount(memoEntity.getScrapCount() + 1);
				memoRepository.save(memoEntity);
			});
		}
	}

	public void unlike(String userId, String memoId) {
		Optional<UserEntity> oUserEntity = userRepository.findById(userId);
		Optional<MemoEntity> oMemoEntity = memoRepository.findById(memoId);
		
		if (oUserEntity.isPresent() && oMemoEntity.isPresent()) {
			UserEntity userEntity = oUserEntity.get();
			MemoEntity memoEntity = oMemoEntity.get();
			
			Optional<UserLikeMemo> oUserLikeMemo = userLikeMemoRepository.findByMemoLikerAndMemoEntity(userEntity, memoEntity);
			oUserLikeMemo.ifPresentOrElse(userLikeMemo -> {
				userLikeMemoRepository.delete(userLikeMemo);
				memoEntity.setLikeCount(memoEntity.getLikeCount() - 1);
				memoRepository.save(memoEntity);
			}, () -> {
				throw new RuntimeException("User did not like memo");
			});
		}
	}

	public void unscrap(String userId, String memoId) {
		Optional<UserEntity> oUserEntity = userRepository.findById(userId);
		Optional<MemoEntity> oMemoEntity = memoRepository.findById(memoId);
		
		if (oUserEntity.isPresent() && oMemoEntity.isPresent()) {
			UserEntity userEntity = oUserEntity.get();
			MemoEntity memoEntity = oMemoEntity.get();
			
			Optional<UserScrapMemo> oUserScrapMemo = userScrapMemoRepository.findByMemoScraperAndMemoEntity(userEntity, memoEntity);
			oUserScrapMemo.ifPresentOrElse(userScrapMemo -> {
				userScrapMemoRepository.delete(userScrapMemo);
				memoEntity.setScrapCount(memoEntity.getScrapCount() - 1);
				memoRepository.save(memoEntity);
			}, () -> {
				throw new RuntimeException("User did not scrap memo");
			});
		}
	}

}
