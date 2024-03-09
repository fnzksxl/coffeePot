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
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody UserRequestDTO userRequestDTO) {
		try {
			UserSignupResponseDTO responseUserDTO = userService.create(userRequestDTO, passwordEncoder);
			return ResponseEntity.status(HttpStatus.CREATED).body(responseUserDTO);
		} catch (Exception e) {
			// 유저 정보는 항상 하나이므로 리스트로 만들어야 하는 ResponseDTO 안 씀
			ResponseDTO responseDTO = ResponseDTO.builder()
					.error(e.getMessage())
					.build();
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}

	@PostMapping("/signin")
	public ResponseEntity<?> authenticate(@RequestBody UserRequestDTO userRequestDTO) {
		try {
			UserSigninResponseDTO signinResponseDTO = userService.signin(userRequestDTO, passwordEncoder);
			return ResponseEntity.ok().body(signinResponseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder()
					.error(e.getMessage())
					.build();
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}

	@PatchMapping("/reissue")
	public ResponseEntity<?> reissueAccessToken(@RequestBody UserRequestDTO userRequestDTO, HttpServletRequest request) {
		try {
			String refreshToken = request.getHeader("Authorization");
			if (StringUtils.hasText(refreshToken) && refreshToken.startsWith("Bearer ")) {
				refreshToken = refreshToken.substring(7);
			}
			
			JWTReissueResponseDTO jwtReissueResponseDTO = userService.reissueAccessToken(userRequestDTO, refreshToken);
			return ResponseEntity.status(HttpStatus.CREATED).body(jwtReissueResponseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder()
					.error(e.getMessage())
					.build();
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}
	
	@PostMapping("/find-username")
	public ResponseEntity<?> findUsername(@RequestBody UserRequestDTO userRequestDTO) {
		try {
			AccountRecoveryResponseDTO responseDTO = userService.getByUsernameInfo(userRequestDTO);
			return ResponseEntity.ok().body(responseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder()
					.error(e.getMessage())
					.build();
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}
	
	@PostMapping("/find-password")
	public ResponseEntity<?> findPassword(@RequestBody UserRequestDTO userRequestDTO) {
		// 비밀번호 이메일로 재발급하기
		try {
			PasswordReissueResponseDTO responseDTO = userService.getByPasswordInfo(userRequestDTO, passwordEncoder);
			return ResponseEntity.ok().body(responseDTO);
		} catch (Exception e) {
			ResponseDTO responseDTO = ResponseDTO.builder()
					.error(e.getMessage())
					.build();
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}

}
