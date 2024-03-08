package com.coffeepot.coffeepotspring.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.coffeepot.coffeepotspring.dto.MemoResponseDTO;
import com.coffeepot.coffeepotspring.dto.MemoSearchParamDTO;
import com.coffeepot.coffeepotspring.dto.ResponseDTO;
import com.coffeepot.coffeepotspring.service.MemoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
	
	private final MemoService memoService;
	
	@GetMapping("/memo")
	public ResponseEntity<?> searchMemoList(
			@ModelAttribute MemoSearchParamDTO memoSearchParamDTO,
			@RequestParam(defaultValue = "") String memoId,
			@RequestParam(defaultValue = "5") int pageSize,
			@RequestParam(defaultValue = "createdAt") String sortBy) {
		try {
			List<MemoResponseDTO> responseDTOs = memoService.retrieveByKeyword(memoSearchParamDTO, memoId, pageSize, sortBy);
			ResponseDTO<MemoResponseDTO> response = ResponseDTO.<MemoResponseDTO>builder().data(responseDTOs).build();
			return ResponseEntity.ok().body(response);
		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO<MemoResponseDTO> response = ResponseDTO.<MemoResponseDTO>builder().error(error).build();
			return ResponseEntity.badRequest().body(response);
		}
	}

}
