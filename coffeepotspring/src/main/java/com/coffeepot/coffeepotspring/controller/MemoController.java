package com.coffeepot.coffeepotspring.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coffeepot.coffeepotspring.dto.MemoDTO;
import com.coffeepot.coffeepotspring.dto.ResponseDTO;
import com.coffeepot.coffeepotspring.model.MemoEntity;
import com.coffeepot.coffeepotspring.service.MemoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/memo")
@RequiredArgsConstructor
public class MemoController {
	
	private final MemoService memoService;
	
	@PostMapping
	public ResponseEntity<?> createMemo(@AuthenticationPrincipal String userId, @RequestBody MemoDTO memoDTO) {
		try {
			MemoEntity memoEntity = MemoDTO.toEntity(memoDTO);
			memoEntity.setUserId(userId);
			List<MemoEntity> memoEntities = memoService.create(memoEntity);
			List<MemoDTO> memoDTOs = memoEntities.stream().map(MemoDTO::new).collect(Collectors.toList());
			ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().data(memoDTOs).build();
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}
	
	@GetMapping
	public ResponseEntity<?> retrieveMemoList(@AuthenticationPrincipal String userId) {
		List<MemoEntity> memoEntities = memoService.retrieveByUserId(userId);
		List<MemoDTO> memoDTOs = memoEntities.stream().map(MemoDTO::new).collect(Collectors.toList());
		ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().data(memoDTOs).build();
		return ResponseEntity.ok().body(response);
	}
	
	@PutMapping
	public ResponseEntity<?> updateMemo(@AuthenticationPrincipal String userId, @RequestBody MemoDTO memoDTO) {
		MemoEntity memoEntity = MemoDTO.toEntity(memoDTO);
		memoEntity.setUserId(userId);
		List<MemoEntity> memoEntities = memoService.update(memoEntity);
		List<MemoDTO> memoDTOs = memoEntities.stream().map(MemoDTO::new).collect(Collectors.toList());
		ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().data(memoDTOs).build();
		return ResponseEntity.ok().body(response);
	}
	
	@DeleteMapping
	public ResponseEntity<?> deleteMemo(@AuthenticationPrincipal String userId, @RequestBody MemoDTO memoDTO) {
		try {
			MemoEntity memoEntity = MemoDTO.toEntity(memoDTO);
			memoEntity.setUserId(userId);
			List<MemoEntity> memoEntities = memoService.delete(memoEntity);
			List<MemoDTO> memoDTOs = memoEntities.stream().map(MemoDTO::new).collect(Collectors.toList());
			ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().data(memoDTOs).build();
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO<MemoDTO> response = ResponseDTO.<MemoDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}

}
