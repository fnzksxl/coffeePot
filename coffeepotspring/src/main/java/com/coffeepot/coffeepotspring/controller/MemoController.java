package com.coffeepot.coffeepotspring.controller;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.coffeepot.coffeepotspring.dto.MemoDTO;
import com.coffeepot.coffeepotspring.dto.ResponseDTO;
import com.coffeepot.coffeepotspring.dto.MemoSearchParamDTO;
import com.coffeepot.coffeepotspring.model.HashTagEntity;
import com.coffeepot.coffeepotspring.model.ImageDataEntity;
import com.coffeepot.coffeepotspring.model.MemoEntity;
import com.coffeepot.coffeepotspring.service.HashTagService;
import com.coffeepot.coffeepotspring.service.ImageService;
import com.coffeepot.coffeepotspring.service.MemoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/memo")
@RequiredArgsConstructor
public class MemoController {
	
	private final MemoService memoService;
	private final HashTagService hashTagService;
	private final ImageService imageService;
	
	private final String MEMO_IMAGE_BASE_PATH = "C:/Users/KWC/Desktop/PKNU/Y2023/CoffePot/coffeePot-BE/coffeepotspring/src/main/resources/memoimages/";
	
	@PostMapping
	public ResponseEntity<?> createMemo(@AuthenticationPrincipal String userId, @ModelAttribute MemoDTO memoDTO) {
		try {
			MemoEntity memoEntity = MemoEntity.builder()
					.userId(userId)
					.title(memoDTO.getTitle())
					.content(memoDTO.getContent())
					.visibility("public".equals(memoDTO.getVisibility()))
					.createdAt(LocalDateTime.now())
					.likeCount(0)
					.scrapCount(0)
					.build();
			MemoEntity createdMemoEntity = memoService.create(memoEntity);
			
			List<HashTagEntity> hashTagEntities = null;
			if (memoDTO.getHashTags() != null) {
				hashTagEntities = hashTagService.create(createdMemoEntity, memoDTO.getHashTags());
			}

			if (memoDTO.getUploadedImages() != null) {
				List<ImageDataEntity> imageDataEntities = new ArrayList<>();
				for (MultipartFile multipartFile : memoDTO.getUploadedImages()) {
					String savedName = createdMemoEntity.getId() + "_" + System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
					File savedFile = new File(MEMO_IMAGE_BASE_PATH + savedName);
					multipartFile.transferTo(savedFile);
					
					imageDataEntities.add(ImageDataEntity.builder()
							.memoEntity(createdMemoEntity)
							.originalName(multipartFile.getOriginalFilename())
							.savedName(savedName)
							.type(multipartFile.getContentType())
							.build());
				}
				imageService.uploadImages(imageDataEntities);
			}

			createdMemoEntity = memoService.retrieveById(createdMemoEntity.getId());
			List<String> hashTags = null;
			if (hashTagEntities != null) {
				hashTags = hashTagEntities.stream().map(hashTag -> hashTag.getHashTag()).toList();
			}
			List<String> imageUrisToBeDownloaded = imageService.retrieveSavedNamesByMemoEntity(createdMemoEntity);
			List<MemoDTO> responseMemoDTO = List.of(new MemoDTO(createdMemoEntity, hashTags, imageUrisToBeDownloaded));
			ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().data(responseMemoDTO).build();
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	@GetMapping("/page")
	public ResponseEntity<?> retrieveAllMemoList(
			@RequestParam(defaultValue = "") String memoId,
			@RequestParam(defaultValue = "5") int pageSize,
			@RequestParam(defaultValue = "createdAt") String sortBy) {
		try {
			Page<MemoEntity> memoPage = memoService.retrieveAll(memoId, pageSize, sortBy);
			List<MemoDTO> memoDTOs = memoPage.get().map(memoEntity -> {
				List<String> hashTags = hashTagService.retrieveByMemoEntity(memoEntity).stream().map(entity -> entity.getHashTag())
						.toList();
				List<String> imageUrisToBeDownloaded = imageService.retrieveSavedNamesByMemoEntity(memoEntity);
				return new MemoDTO(memoEntity, hashTags, imageUrisToBeDownloaded);
			}).toList();
			ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().data(memoDTOs).build();
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	@GetMapping
	public ResponseEntity<?> retrieveAllMemoList() {
		List<MemoEntity> memoEntities = memoService.retrieveAll();
		List<MemoDTO> memoDTOs = memoEntities.stream().map(memoEntity -> {
			List<String> hashTags = hashTagService.retrieveByMemoEntity(memoEntity).stream().map(entity -> {
				return entity.getHashTag();
			}).toList();
			List<String> imageUrisToBeDownloaded = imageService.retrieveSavedNamesByMemoEntity(memoEntity);
			return new MemoDTO(memoEntity, hashTags, imageUrisToBeDownloaded);
		}).toList();
		ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().data(memoDTOs).build();
		return ResponseEntity.ok().body(response);
	}
	
	@GetMapping("/my-memo-page")
	public ResponseEntity<?> retrieveMyMemoList(
			@AuthenticationPrincipal String userId,
			@RequestParam(defaultValue = "") String memoId,
			@RequestParam(defaultValue = "5") int pageSize,
			@RequestParam(defaultValue = "createdAt") String sortBy) {
		try {
			Page<MemoEntity> memoPage = memoService.retrieveByUserId(userId, memoId, pageSize, sortBy);
			List<MemoDTO> memoDTOs = memoPage.get().map(memoEntity -> {
				List<String> hashTags = hashTagService.retrieveByMemoEntity(memoEntity).stream().map(entity -> entity.getHashTag())
						.toList();
				List<String> imageUrisToBeDownloaded = imageService.retrieveSavedNamesByMemoEntity(memoEntity);
				return new MemoDTO(memoEntity, hashTags, imageUrisToBeDownloaded);
			}).toList();
			ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().data(memoDTOs).build();
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	@GetMapping("/my-memo")
	public ResponseEntity<?> retrieveMyMemoList(@AuthenticationPrincipal String userId) {
		List<MemoEntity> memoEntities = memoService.retrieveByUserId(userId);
		List<MemoDTO> memoDTOs = memoEntities.stream().map(memoEntity -> {
			List<String> hashTags = hashTagService.retrieveByMemoEntity(memoEntity).stream().map(entity -> {
				return entity.getHashTag();
			}).toList();
			List<String> imageUrisToBeDownloaded = imageService.retrieveSavedNamesByMemoEntity(memoEntity);
			return new MemoDTO(memoEntity, hashTags, imageUrisToBeDownloaded);
		}).toList();
		ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().data(memoDTOs).build();
		return ResponseEntity.ok().body(response);
	}
	
	@PutMapping
	public ResponseEntity<?> updateMemo(@AuthenticationPrincipal String userId, @ModelAttribute MemoDTO memoDTO) {
		try {
			MemoEntity memoEntity = MemoEntity.builder()
					.id(memoDTO.getId())
					.title(memoDTO.getTitle())
					.content(memoDTO.getContent())
					.visibility("public".equals(memoDTO.getVisibility()))
					.build();
			memoEntity.setUserId(userId);
			MemoEntity updatedMemoEntity = memoService.update(memoEntity);
			
			List<HashTagEntity> hashTagEntities = hashTagService.update(updatedMemoEntity, memoDTO.getHashTags());
			
			imageService.deleteByMemoEntity(updatedMemoEntity);
			if (memoDTO.getUploadedImages() != null) {
				List<ImageDataEntity> imageDataEntities = new ArrayList<>();
				for (MultipartFile multipartFile : memoDTO.getUploadedImages()) {
					String savedName = updatedMemoEntity.getId() + "_" + System.currentTimeMillis() + "_" + multipartFile.getOriginalFilename();
					File savedFile = new File(MEMO_IMAGE_BASE_PATH + savedName);
					multipartFile.transferTo(savedFile);
					
					imageDataEntities.add(ImageDataEntity.builder()
							.memoEntity(updatedMemoEntity)
							.originalName(multipartFile.getOriginalFilename())
							.savedName(savedName)
							.type(multipartFile.getContentType())
							.build());
				}
				imageService.uploadImages(imageDataEntities);
			}
			List<String> hashTags = hashTagEntities.stream().map(entity -> entity.getHashTag()).toList();
			List<String> imageUrisToBeDownloaded = imageService.retrieveSavedNamesByMemoEntity(memoEntity);
			List<MemoDTO> memoDTOs = List.of(new MemoDTO(updatedMemoEntity, hashTags, imageUrisToBeDownloaded));
			ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().data(memoDTOs).build();
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	@DeleteMapping
	public ResponseEntity<?> deleteMemo(@AuthenticationPrincipal String userId, @RequestBody MemoDTO memoDTO) {
		try {
			MemoEntity memoEntity = MemoEntity.builder()
					.id(memoDTO.getId())
					.userId(userId)
					.build();
			imageService.deleteByMemoEntity(memoEntity);
			
			memoService.delete(memoEntity);
			List<MemoEntity> memoEntities = memoService.retrieveByUserId(userId);
			List<MemoDTO> memoDTOs = memoEntities.stream().map(entity -> {
				List<String> hashTags = hashTagService.retrieveByMemoEntity(entity).stream()
						.map(hashTagEntity -> hashTagEntity.getHashTag()).toList();
				List<String> imageUrisToBeDownloaded = imageService.retrieveSavedNamesByMemoEntity(entity);
				return new MemoDTO(entity, hashTags, imageUrisToBeDownloaded);
			}).toList();
			ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().data(memoDTOs).build();
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	@PostMapping("/like")
	public ResponseEntity<?> likeMemo(@AuthenticationPrincipal String userId, @RequestParam String memoId) {
		try {
			memoService.like(userId, memoId);
			return ResponseEntity.ok(null);
		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	@PostMapping("/scrap")
	public ResponseEntity<?> scrapMemo(@AuthenticationPrincipal String userId, @RequestParam String memoId) {
		try {
			memoService.scrap(userId, memoId);
			return ResponseEntity.ok(null);
		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	@DeleteMapping("/like")
	public ResponseEntity<?> unlikeMemo(@AuthenticationPrincipal String userId, @RequestParam String memoId) {
		try {
			memoService.unlike(userId, memoId);
			return ResponseEntity.ok(null);
		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	@DeleteMapping("/scrap")
	public ResponseEntity<?> unscrapMemo(@AuthenticationPrincipal String userId, @RequestParam String memoId) {
		try {
			memoService.unscrap(userId, memoId);
			return ResponseEntity.ok(null);
		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}

}
