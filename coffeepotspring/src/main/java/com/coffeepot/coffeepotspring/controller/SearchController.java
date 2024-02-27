package com.coffeepot.coffeepotspring.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coffeepot.coffeepotspring.dto.MemoDTO;
import com.coffeepot.coffeepotspring.dto.MemoSearchParamDTO;
import com.coffeepot.coffeepotspring.dto.ResponseDTO;
import com.coffeepot.coffeepotspring.model.MemoEntity;
import com.coffeepot.coffeepotspring.service.HashTagService;
import com.coffeepot.coffeepotspring.service.ImageService;
import com.coffeepot.coffeepotspring.service.MemoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
	
	private final MemoService memoService;
	private final HashTagService hashTagService;
	private final ImageService imageService;
	
	@GetMapping("/memo")
	public ResponseEntity<?> searchMemoList(
			@ModelAttribute MemoSearchParamDTO memoSearchParamDTO,
			@RequestParam(defaultValue = "") String memoId,
			@RequestParam(defaultValue = "5") int pageSize,
			@RequestParam(defaultValue = "createdAt") String sortBy) {
		try {
			List<MemoEntity> memoPage = memoService.retrieveByKeyword(memoSearchParamDTO, memoId, pageSize, sortBy);
			List<MemoDTO> memoDTOs = memoPage.stream().map(memoEntity -> {
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

}
