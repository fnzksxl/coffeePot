package com.coffeepot.coffeepotspring.controller;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springdoc.core.annotations.ParameterObject;
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
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Tag(name = "memo-controller", description = "Swagger UI에서는 uploadedImages를 비울 경우 에러 발생. "
		+ "Swagger UI 외에는 uploadedImages가 비어도 정상 동작.")
@RequestMapping("/memo")
@RequiredArgsConstructor
public class MemoController {
	
	private final MemoService memoService;
	
	@Operation(summary = "새 메모 생성", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
					mediaType = "multipart/form-data",
					schema = @Schema(allOf = MemoRequestDTO.class, requiredProperties = { "title", "content", "visibility" }))))
	@SecurityRequirement(name = "Signin Authentication")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "메모 생성 성공", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = MemoResponseDTOWrapper.class))
		}),
		@ApiResponse(responseCode = "400", description = "메모 생성 실패", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		}),
		@ApiResponse(responseCode = "401", description = "인가되지 않은 사용자", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		}),
		@ApiResponse(responseCode = "500", description = "서버 측 오류", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		})
	})
	@PostMapping
	public ResponseEntity<?> createMemo(@AuthenticationPrincipal String userId,
			@ModelAttribute MemoRequestDTO memoRequestDTO) {
		try {
			List<MemoResponseDTO> responseDTOs = List.of(memoService.create(userId, memoRequestDTO));
			ResponseDTO<MemoResponseDTO> response = ResponseDTO.<MemoResponseDTO>builder().data(responseDTOs).build();
			return ResponseEntity.created(URI.create("/memo/details/" + responseDTOs.get(0).getId())).body(response);
		} catch (IOException e) {
			String error = e.getMessage();
			ResponseDTO<MemoResponseDTO> response = ResponseDTO.<MemoResponseDTO>builder().error(error).build();
			return ResponseEntity.internalServerError().body(response);
		}
	}
	
	@Operation(summary = "메모 1개 조회")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "메모 조회 성공", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = MemoResponseDTOWrapper.class))
		}),
		@ApiResponse(responseCode = "400", description = "메모 조회 실패", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		}),
		@ApiResponse(responseCode = "404", description = "메모를 찾을 수 없음", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		})
	})
	@GetMapping("/details/{memoId}")
	public ResponseEntity<?> retrieveMemo(@PathVariable String memoId) {
		List<MemoResponseDTO> responseDTOs = memoService.retrieveById(memoId);
		ResponseDTO<MemoResponseDTO> response = ResponseDTO.<MemoResponseDTO>builder().data(responseDTOs).build();
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "메모 조회", description = "memoId: 커서로 사용할 메모의 id - 안 적을 경우 최상위 메모 조회\n\n"
			+ "pageSize: 한 번에 조회할 메모 개수\n\nsortBy: 메모 정렬 기준 (createdAt만 가능)")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "메모 조회 성공", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = MemoResponseDTOWrapper.class))
		}),
		@ApiResponse(responseCode = "400", description = "메모 조회 실패", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		}),
		@ApiResponse(responseCode = "404", description = "메모를 찾을 수 없음", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		})
	})
	@GetMapping("/page")
	public ResponseEntity<?> retrieveAllMemoList(
			@RequestParam(defaultValue = "") String memoId,
			@RequestParam(defaultValue = "5") int pageSize,
			@RequestParam(defaultValue = "createdAt") String sortBy) {
		List<MemoResponseDTO> responseDTOs = memoService.retrieve(memoId, pageSize, sortBy);
		ResponseDTO<MemoResponseDTO> response = ResponseDTO.<MemoResponseDTO>builder().data(responseDTOs).build();
		return ResponseEntity.ok().body(response);
	}

	@Operation(summary = "사용자가 쓴 메모 조회", description = "memoId: 커서로 사용할 메모의 id - 안 적을 경우 최상위 메모 조회\n\n"
			+ "pageSize: 한 번에 조회할 메모 개수\n\nsortBy: 메모 정렬 기준 (createdAt만 가능)")
	@SecurityRequirement(name = "Signin Authentication")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "메모 조회 성공", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = MemoResponseDTOWrapper.class))
		}),
		@ApiResponse(responseCode = "400", description = "메모 조회 실패", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		}),
		@ApiResponse(responseCode = "401", description = "인가되지 않은 사용자", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		}),
		@ApiResponse(responseCode = "404", description = "메모를 찾을 수 없음", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		})
	})
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

	@Operation(summary = "메모 수정", requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
			content = @Content(
					mediaType = "multipart/form-data",
					schema = @Schema(allOf = MemoRequestDTO.class, requiredProperties = { "id", "title", "content", "visibility" }))))
	@SecurityRequirement(name = "Signin Authentication")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "메모 수정 성공", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = MemoResponseDTOWrapper.class))
		}),
		@ApiResponse(responseCode = "400", description = "메모 수정 실패", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		}),
		@ApiResponse(responseCode = "401", description = "인가되지 않은 사용자", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		}),
		@ApiResponse(responseCode = "500", description = "서버 측 오류", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		})
	})
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

	@Operation(summary = "메모 삭제", description = "memoId: 삭제하려는 메모의 id")
	@SecurityRequirement(name = "Signin Authentication")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "메모 삭제 성공"),
		@ApiResponse(responseCode = "400", description = "메모 삭제 실패", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		}),
		@ApiResponse(responseCode = "401", description = "인가되지 않은 사용자", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		}),
		@ApiResponse(responseCode = "404", description = "메모를 찾을 수 없음", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		})
	})
	@DeleteMapping("/{memoId}")
	public ResponseEntity<?> deleteMemo(
			@AuthenticationPrincipal String userId, @PathVariable String memoId) {
		memoService.delete(userId, memoId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "메모 좋아요", description = "memoId: 좋아요하려는 메모의 id")
	@SecurityRequirement(name = "Signin Authentication")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "메모 좋아요 성공"),
		@ApiResponse(responseCode = "400", description = "메모 좋아요 실패", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		}),
		@ApiResponse(responseCode = "401", description = "인가되지 않은 사용자", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		}),
		@ApiResponse(responseCode = "404", description = "메모를 찾을 수 없음", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		})
	})
	@PostMapping("/like")
	public ResponseEntity<?> likeMemo(@AuthenticationPrincipal String userId,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
					examples = @ExampleObject(value = "{\"memoId\": \"string\"}")))
			@RequestBody Map<String, String> memoId) {
		memoService.like(userId, memoId.get("memoId"));
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "메모 좋아요 취소", description = "memoId: 좋아요 취소하려는 메모의 id")
	@SecurityRequirement(name = "Signin Authentication")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "메모 좋아요 취소 성공"),
		@ApiResponse(responseCode = "400", description = "메모 좋아요 취소 실패", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		}),
		@ApiResponse(responseCode = "401", description = "인가되지 않은 사용자", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		}),
		@ApiResponse(responseCode = "404", description = "메모를 찾을 수 없음", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		})
	})
	@DeleteMapping("/like/{memoId}")
	public ResponseEntity<?> unlikeMemo(@AuthenticationPrincipal String userId, @PathVariable String memoId) {
		memoService.unlike(userId, memoId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "메모 스크랩", description = "memoId: 스크랩하려는 메모의 id")
	@SecurityRequirement(name = "Signin Authentication")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "메모 스크랩 성공"),
		@ApiResponse(responseCode = "400", description = "메모 스크랩 실패"),
		@ApiResponse(responseCode = "401", description = "인가되지 않은 사용자", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		}),
		@ApiResponse(responseCode = "404", description = "메모를 찾을 수 없음")
	})
	@PostMapping("/scrap")
	public ResponseEntity<?> scrapMemo(@AuthenticationPrincipal String userId,
			@io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
					examples = @ExampleObject(value = "{\"memoId\": \"string\"}")))
			@RequestBody Map<String, String> memoId) {
		memoService.scrap(userId, memoId.get("memoId"));
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@Operation(summary = "메모 스크랩 취소", description = "memoId: 스크랩 취소하려는 메모의 id")
	@SecurityRequirement(name = "Signin Authentication")
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "메모 스크랩 취소 성공"),
		@ApiResponse(responseCode = "400", description = "메모 스크랩 취소 실패"),
		@ApiResponse(responseCode = "401", description = "인가되지 않은 사용자", content = {
				@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
		}),
		@ApiResponse(responseCode = "404", description = "메모를 찾을 수 없음")
	})
	@DeleteMapping("/scrap/{memoId}")
	public ResponseEntity<?> unscrapMemo(@AuthenticationPrincipal String userId, @PathVariable String memoId) {
		memoService.unscrap(userId, memoId);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
