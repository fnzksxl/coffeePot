package com.coffeepot.coffeepotspring.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.coffeepot.coffeepotspring.dto.AccountRecoveryResponseDTO;
import com.coffeepot.coffeepotspring.dto.JWTReissueResponseDTO;
import com.coffeepot.coffeepotspring.dto.PasswordReissueResponseDTO;
import com.coffeepot.coffeepotspring.dto.ResponseDTO;
import com.coffeepot.coffeepotspring.dto.UserRequestDTO;
import com.coffeepot.coffeepotspring.dto.UserSigninResponseDTO;
import com.coffeepot.coffeepotspring.dto.UserSignupResponseDTO;
import com.coffeepot.coffeepotspring.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@Operation(summary = "유저 정보로 회원 가입")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "회원 가입 성공", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = UserSignupResponseDTO.class))
			}),
			@ApiResponse(responseCode = "400", description = "회원 가입 실패", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
			})
	})
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody UserRequestDTO userRequestDTO) {
		UserSignupResponseDTO responseUserDTO = userService.create(userRequestDTO, passwordEncoder);
		return ResponseEntity.status(HttpStatus.CREATED).body(responseUserDTO);
	}

	@Operation(summary = "username과 password로 로그인", description = "email은 불필요")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "로그인 성공", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = UserSigninResponseDTO.class))
			}),
			@ApiResponse(responseCode = "400", description = "로그인 실패", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
			})
	})
	@PostMapping("/signin")
	public ResponseEntity<?> authenticate(@RequestBody UserRequestDTO userRequestDTO) {
		UserSigninResponseDTO signinResponseDTO = userService.signin(userRequestDTO, passwordEncoder);
		return ResponseEntity.ok().body(signinResponseDTO);
	}

	@Operation(summary = "Access token 재발급", description = "헤더의 Authorization에 refresh token 필요\n\n"
			+ "Request body에서 username만 필요")
	@SecurityRequirement(name = "Reissue Authentication")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "재발급 성공", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = JWTReissueResponseDTO.class))
			}),
			@ApiResponse(responseCode = "400", description = "재발급 실패", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
			})
	})
	@PatchMapping("/reissue")
	public ResponseEntity<?> reissueAccessToken(@RequestBody UserRequestDTO userRequestDTO, HttpServletRequest request) {
		String refreshToken = request.getHeader("Authorization");
		if (StringUtils.hasText(refreshToken) && refreshToken.startsWith("Bearer ")) {
			refreshToken = refreshToken.substring(7);
		}
		
		JWTReissueResponseDTO jwtReissueResponseDTO = userService.reissueAccessToken(userRequestDTO, refreshToken);
		return ResponseEntity.ok().body(jwtReissueResponseDTO);
	}

	@Operation(summary = "유저 정보로 username 찾기")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Username 찾기 성공", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = AccountRecoveryResponseDTO.class))
			}),
			@ApiResponse(responseCode = "400", description = "Username 찾기 실패", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
			})
	})
	@PostMapping("/find-username")
	public ResponseEntity<?> findUsername(@RequestBody UserRequestDTO userRequestDTO) {
		AccountRecoveryResponseDTO responseDTO = userService.getByUsernameInfo(userRequestDTO);
		return ResponseEntity.ok().body(responseDTO);
	}

	@Operation(summary = "유저 정보로 password 찾기")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Password 재설정 성공", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = PasswordReissueResponseDTO.class))
			}),
			@ApiResponse(responseCode = "400", description = "Password 재설정 실패", content = {
					@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDTO.class))
			})
	})
	@PostMapping("/find-password")
	public ResponseEntity<?> findPassword(@RequestBody UserRequestDTO userRequestDTO) {
		// 비밀번호 이메일로 재발급하기
		PasswordReissueResponseDTO responseDTO = userService.getByPasswordInfo(userRequestDTO, passwordEncoder);
		return ResponseEntity.ok().body(responseDTO);
	}

}
