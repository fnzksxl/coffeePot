package com.coffeepot.coffeepotspring.controller;

import java.util.List;

import org.springdoc.core.annotations.ParameterObject;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
	
	private final MemoService memoService;

	@Operation(summary = "메모 검색", description = "titleKeyword: 제목 검색 키워드\n\n"
			+ "contentKeyword: 내용 검색 키워드\n\n"
			+ "authorKeyword: 글쓴이 검색 키워드")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "메모 검색 성공"),
		@ApiResponse(responseCode = "400", description = "메모 검색 실패"),
		@ApiResponse(responseCode = "404", description = "메모를 찾을 수 없음")
	})
	@GetMapping("/memo")
	public ResponseEntity<?> searchMemoList(
			@ModelAttribute @ParameterObject MemoSearchParamDTO memoSearchParamDTO,
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
