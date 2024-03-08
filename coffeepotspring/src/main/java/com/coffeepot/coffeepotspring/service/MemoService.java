package com.coffeepot.coffeepotspring.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.coffeepot.coffeepotspring.dto.MemoRequestDTO;
import com.coffeepot.coffeepotspring.dto.MemoResponseDTO;
import com.coffeepot.coffeepotspring.dto.MemoSearchParamDTO;
import com.coffeepot.coffeepotspring.model.HashTagEntity;
import com.coffeepot.coffeepotspring.model.ImageDataEntity;
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
	
	private final HashTagService hashTagService;
	private final ImageService imageService;
	private final MemoRepository memoRepository;
	private final UserRepository userRepository;
	private final UserLikeMemoRepository userLikeMemoRepository;
	private final UserScrapMemoRepository userScrapMemoRepository;
	
	private Page<MemoEntity> retrievePageByCursor(final String memoId, final Pageable pageable) {
		if ("".equals(memoId)) {
			Optional<MemoEntity> oMemoEntity = memoRepository.findFirstByOrderByCreatedAtDesc();
			MemoEntity memoEntity = oMemoEntity.get();
			return memoRepository.findAllByCreatedAtLessThanEqualOrderByCreatedAtDesc(
					memoEntity.getCreatedAt(), pageable);
		} else {
			Optional<MemoEntity> oMemoEntity = memoRepository.findById(memoId);
			MemoEntity memoEntity = oMemoEntity.get();
			return memoRepository.findAllByCreatedAtLessThanOrderByCreatedAtDesc(
					memoEntity.getCreatedAt(), pageable);
		}
	}
	
	private void validateAuthor(final String userId, final MemoEntity memoEntity) {
		if (!userId.equals(memoEntity.getUserId())) {
			throw new RuntimeException("User is not the author of the memo");
		}
	}
	
	public MemoResponseDTO create(final String userId, final MemoRequestDTO memoRequestDTO) throws IOException {
		MemoEntity memoEntity = MemoEntity.builder()
				.userId(userId)
				.title(memoRequestDTO.getTitle())
				.content(memoRequestDTO.getContent())
				.visibility("public".equals(memoRequestDTO.getVisibility()))
				.createdAt(LocalDateTime.now())
				.likeCount(0)
				.scrapCount(0)
				.build();
		MemoEntity createdMemoEntity = memoRepository.save(memoEntity);
		
		List<HashTagEntity> hashTagEntities = null;
		if (memoRequestDTO.getHashTags() != null) {
			hashTagEntities = hashTagService.create(createdMemoEntity, memoRequestDTO.getHashTags());
		}
		
		List<ImageDataEntity> imageDataEntities = null;
		if (memoRequestDTO.getUploadedImages() != null) {
			imageDataEntities = imageService.uploadImages(createdMemoEntity, memoRequestDTO.getUploadedImages());
		}
		
		return MemoResponseDTO.of(createdMemoEntity, hashTagEntities, imageDataEntities);
	}
	
	public List<MemoResponseDTO> retrieve(final String memoId, final int pageSize, final String sortBy) {
		Pageable pageable = PageRequest.of(0, pageSize, Sort.by(sortBy).descending());
		Page<MemoEntity> memoEntities = retrievePageByCursor(memoId, pageable);
		
		return memoEntities.get().map(memoEntity -> {
			List<HashTagEntity> hashTagEntities = hashTagService.retrieveByMemoEntity(memoEntity);
			List<ImageDataEntity> imageDataEntities = imageService.retrieveByMemoEntity(memoEntity);
			return MemoResponseDTO.of(memoEntity, hashTagEntities, imageDataEntities);
		}).toList();
	}
	
	// 자식 엔티티를 호출할 경우 메소드에 @Transactional을 추가해야 함
	public List<MemoResponseDTO> retrieveMyMemoPage(
			final String userId, final String memoId, final int pageSize, final String sortBy) {
		Pageable pageable = PageRequest.of(0, pageSize, Sort.by(sortBy).descending());
		Page<MemoEntity> memoEntities = null;
		if ("".equals(memoId)) {
			Optional<MemoEntity> oMemoEntity = memoRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
			MemoEntity memoEntity = oMemoEntity.get();
			memoEntities = memoRepository.findAllByUserIdAndCreatedAtLessThanEqualOrderByCreatedAtDesc(
					userId, memoEntity.getCreatedAt(), pageable);
		} else {
			Optional<MemoEntity> oMemoEntity = memoRepository.findById(memoId);
			MemoEntity memoEntity = oMemoEntity.get();
			memoEntities = memoRepository.findAllByUserIdAndCreatedAtLessThanOrderByCreatedAtDesc(
					userId, memoEntity.getCreatedAt(), pageable);
		}
		
		return memoEntities.get().map(memoEntity -> {
			List<HashTagEntity> hashTagEntities = hashTagService.retrieveByMemoEntity(memoEntity);
			List<ImageDataEntity> imageDataEntities = imageService.retrieveByMemoEntity(memoEntity);
			return MemoResponseDTO.of(memoEntity, hashTagEntities, imageDataEntities);
		}).toList();
	}
	
	public MemoResponseDTO update(final String userId, final MemoRequestDTO memoRequestDTO) throws IOException {
		MemoEntity memoEntity = memoRepository.findById(memoRequestDTO.getId()).get();
		validateAuthor(userId, memoEntity);
		
		memoEntity.setTitle(memoRequestDTO.getTitle());
		memoEntity.setContent(memoRequestDTO.getContent());
		memoEntity.setVisibility("public".equals(memoRequestDTO.getVisibility()));
		memoEntity.setUpdatedAt(LocalDateTime.now());
		
		MemoEntity updatedMemoEntity = memoRepository.save(memoEntity);
		List<HashTagEntity> hashTagEntities = hashTagService.update(updatedMemoEntity, memoRequestDTO.getHashTags());
		List<ImageDataEntity> imageDataEntities = imageService.update(updatedMemoEntity, memoRequestDTO.getUploadedImages());
		
		return MemoResponseDTO.of(updatedMemoEntity, hashTagEntities, imageDataEntities);
	}
	
	public void delete(final String userId, final String memoId) {
		MemoEntity memoEntity = memoRepository.findById(memoId).get();
		validateAuthor(userId, memoEntity);
		memoRepository.delete(memoEntity);
	}
	
	public void like(final String userId, final String memoId) {
		Optional<UserEntity> oUserEntity = userRepository.findById(userId);
		Optional<MemoEntity> oMemoEntity = memoRepository.findById(memoId);
		
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

	public void unlike(String userId, String memoId) {
		Optional<UserEntity> oUserEntity = userRepository.findById(userId);
		Optional<MemoEntity> oMemoEntity = memoRepository.findById(memoId);
		
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
	
	public void scrap(final String userId, final String memoId) {
		Optional<UserEntity> oUserEntity = userRepository.findById(userId);
		Optional<MemoEntity> oMemoEntity = memoRepository.findById(memoId);
		
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

	public void unscrap(String userId, String memoId) {
		Optional<UserEntity> oUserEntity = userRepository.findById(userId);
		Optional<MemoEntity> oMemoEntity = memoRepository.findById(memoId);
		
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

	public List<MemoResponseDTO> retrieveByKeyword(MemoSearchParamDTO memoSearchParamDTO, String memoId, int pageSize, String sortBy) {
		List<MemoEntity> memoEntities = null;
		if ("".equals(memoId)) {
			memoEntities = memoRepository.findFirstNBySearchParamOrderByCreatedAtDesc(memoSearchParamDTO, (long) pageSize);
		} else {
			memoEntities = memoRepository.findNBySearchParamOrderByCreatedAtDesc(memoSearchParamDTO, memoId, (long) pageSize);
		}
		
		return memoEntities.stream().map(memoEntity -> {
			List<HashTagEntity> hashTagEntities = hashTagService.retrieveByMemoEntity(memoEntity);
			List<ImageDataEntity> imageDataEntities = imageService.retrieveByMemoEntity(memoEntity);
			return MemoResponseDTO.of(memoEntity, hashTagEntities, imageDataEntities);
		}).toList();
	}

}
