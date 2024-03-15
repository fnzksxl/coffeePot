package com.coffeepot.coffeepotspring.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coffeepot.coffeepotspring.dto.MemoRequestDTO;
import com.coffeepot.coffeepotspring.dto.MemoResponseDTO;
import com.coffeepot.coffeepotspring.dto.ResponseDTO;
import com.coffeepot.coffeepotspring.dto.wrapper.MemoResponseDTOWrapper;
import com.coffeepot.coffeepotspring.service.MemoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/memo")
@RequiredArgsConstructor
public class MemoController {
	
	private final MemoService memoService;
	
	@Operation(summary = "새 메모 생성", description = "id 불필요, hashTags, uploadedImages는 선택, 나머지는 필수")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "메모 생성 성공", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = MemoResponseDTOWrapper.class))
		})
	})
	@PostMapping
	public ResponseEntity<?> createMemo(@AuthenticationPrincipal String userId, @ModelAttribute MemoRequestDTO memoRequestDTO) {
		try {
			List<MemoResponseDTO> responseDTOs = List.of(memoService.create(userId, memoRequestDTO));
			ResponseDTO<MemoResponseDTO> response = ResponseDTO.<MemoResponseDTO>builder().data(responseDTOs).build();
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (IOException e) {
			String error = e.getMessage();
			ResponseDTO<MemoResponseDTO> response = ResponseDTO.<MemoResponseDTO>builder().error(error).build();
			return ResponseEntity.internalServerError().body(response);
		}
	}
	
	@GetMapping("/page")
	public ResponseEntity<?> retrieveAllMemoList(
			@RequestParam(defaultValue = "") String memoId,
			@RequestParam(defaultValue = "5") int pageSize,
			@RequestParam(defaultValue = "createdAt") String sortBy) {
		List<MemoResponseDTO> responseDTOs = memoService.retrieve(memoId, pageSize, sortBy);
		ResponseDTO<MemoResponseDTO> response = ResponseDTO.<MemoResponseDTO>builder().data(responseDTOs).build();
		return ResponseEntity.ok().body(response);
	}
	
	@GetMapping("/my-memo-page")
	public ResponseEntity<?> retrieveMyMemoList(
			@AuthenticationPrincipal String userId,
			@RequestParam(defaultValue = "") String memoId,
			@RequestParam(defaultValue = "5") int pageSize,
			@RequestParam(defaultValue = "createdAt") String sortBy) {
		List<MemoResponseDTO> responseDTOs = memoService.retrieveMyMemoPage(userId, memoId, pageSize, sortBy);
		ResponseDTO<MemoResponseDTO> response = ResponseDTO.<MemoResponseDTO>builder().data(responseDTOs).build();
		return ResponseEntity.ok().body(response);
	}
	
	@PutMapping
	public ResponseEntity<?> updateMemo(@AuthenticationPrincipal String userId, @ModelAttribute MemoRequestDTO memoRequestDTO) {
		try {
			List<MemoResponseDTO> responseDTOs = List.of(memoService.update(userId, memoRequestDTO));
			ResponseDTO<MemoResponseDTO> response = ResponseDTO.<MemoResponseDTO>builder().data(responseDTOs).build();
			return ResponseEntity.ok().body(response);
		} catch (IOException e) {
			String error = e.getMessage();
			ResponseDTO<MemoResponseDTO> response = ResponseDTO.<MemoResponseDTO>builder().error(error).build();
			return ResponseEntity.internalServerError().body(response);
		}
	}
	
	@DeleteMapping("/{memoId}")
	public ResponseEntity<?> deleteMemo(
			@AuthenticationPrincipal String userId, @PathVariable String memoId) {
		memoService.delete(userId, memoId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@PostMapping("/like")
	public ResponseEntity<?> likeMemo(@AuthenticationPrincipal String userId, @RequestBody Map<String, String> memoId) {
		memoService.like(userId, memoId.get("memoId"));
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@DeleteMapping("/like/{memoId}")
	public ResponseEntity<?> unlikeMemo(@AuthenticationPrincipal String userId, @PathVariable String memoId) {
		memoService.unlike(userId, memoId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@PostMapping("/scrap")
	public ResponseEntity<?> scrapMemo(@AuthenticationPrincipal String userId, @RequestBody Map<String, String> memoId) {
		memoService.scrap(userId, memoId.get("memoId"));
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@DeleteMapping("/scrap/{memoId}")
	public ResponseEntity<?> unscrapMemo(@AuthenticationPrincipal String userId, @PathVariable String memoId) {
		memoService.unscrap(userId, memoId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
