package com.coffeepot.coffeepotspring.service;

import java.util.List;
import java.util.Random;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.coffeepot.coffeepotspring.dto.AccountRecoveryResponseDTO;
import com.coffeepot.coffeepotspring.dto.JWTReissueResponseDTO;
import com.coffeepot.coffeepotspring.dto.PasswordReissueResponseDTO;
import com.coffeepot.coffeepotspring.dto.UserRequestDTO;
import com.coffeepot.coffeepotspring.dto.UserSigninResponseDTO;
import com.coffeepot.coffeepotspring.dto.UserSignupResponseDTO;
import com.coffeepot.coffeepotspring.model.UserEntity;
import com.coffeepot.coffeepotspring.persistence.UserRepository;
import com.coffeepot.coffeepotspring.security.TokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final TokenProvider tokenProvider;
	private final UserRepository userRepository;
	
	public UserSignupResponseDTO create(final UserRequestDTO userRequestDTO, PasswordEncoder passwordEncoder) {
		if (userRequestDTO == null || userRequestDTO.getUsername() == null) {
			throw new RuntimeException("Invalid arguments");
		}
		if (userRequestDTO.getPassword() == null) {
			throw new RuntimeException("Invalid Password value.");
		}
		
		final String username = userRequestDTO.getUsername();
		if (userRepository.existsByUsername(username)) {
			log.warn("Username already exists {}", username);
			throw new RuntimeException("Username already exists");
		}
		final String password = userRequestDTO.getPassword();
		
		UserEntity userEntity = UserEntity.builder()
				.username(username)
				.password(passwordEncoder.encode(password))
				.email(userRequestDTO.getEmail())
				.build();
		UserEntity createdUserEntity = userRepository.save(userEntity);
		
		return UserSignupResponseDTO.builder()
				.id(createdUserEntity.getId())
				.username(createdUserEntity.getUsername())
				.build();
	}
	
	public UserSigninResponseDTO signin(final UserRequestDTO userRequestDTO, final PasswordEncoder encoder) {
		final UserEntity originalUser = userRepository.findByUsername(userRequestDTO.getUsername());
		
		if (originalUser != null && encoder.matches(userRequestDTO.getPassword(), originalUser.getPassword())) {
			String accessToken = tokenProvider.createAccessToken(originalUser.getId());
			String refreshToken = tokenProvider.createRefreshToken(originalUser.getId());
			
			return UserSigninResponseDTO.builder()
					.id(originalUser.getId())
					.username(originalUser.getUsername())
					.accessToken(accessToken)
					.refreshToken(refreshToken)
					.build();
		}
		return null;
	}
	
	public JWTReissueResponseDTO reissueAccessToken(final UserRequestDTO userRequestDTO, final String refreshToken) {
		UserEntity userEntity = userRepository.findByUsername(userRequestDTO.getUsername());
		String accessToken = tokenProvider.validateAndReissueAccessToken(refreshToken, userEntity.getId());
		return JWTReissueResponseDTO.builder()
				.id(userEntity.getId())
				.username(userEntity.getUsername())
				.accessToken(accessToken)
				.build();
	}
	
	// TODO 아이디 찾기
	// 가입 정보 정해지면 구체적으로 구현하기
	// 일단 이메일로 임시 구현
	public AccountRecoveryResponseDTO getByUsernameInfo(UserRequestDTO userRequestDTO) {
		UserEntity userEntity = userRepository.findByEmail(userRequestDTO.getEmail()).orElseThrow();
		return AccountRecoveryResponseDTO.builder()
				.username(userEntity.getUsername())
				.build();
	}
	
	public PasswordReissueResponseDTO getByPasswordInfo(UserRequestDTO userRequestDTO, PasswordEncoder passwordEncoder) {
		validatePasswordInfo(userRequestDTO);
		String newPassword = reissuePassword();
		
		UserEntity originalUserEntity = userRepository.findByUsername(userRequestDTO.getUsername());
		originalUserEntity.updatePassword(passwordEncoder.encode(newPassword));
		userRepository.save(originalUserEntity);
		
		return PasswordReissueResponseDTO.builder()
				.password(newPassword)
				.build();
	}
	
	private void validatePasswordInfo(UserRequestDTO userRequestDTO) {
		if (userRequestDTO.getUsername() != null && !userRepository.existsByUsername(userRequestDTO.getUsername())) {
			throw new RuntimeException("Invalid username");
		}
		if (userRequestDTO.getEmail() != null && !userRepository.existsByEmail(userRequestDTO.getEmail())) {
			throw new RuntimeException("Invalid email");
		}
	}
	
	private String reissuePassword() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 10; i++) {
			int charSelector = (int) (Math.random() * 62);
			
			if (charSelector < 10) {
				sb.append((char) ('0' + (Math.random() * 10)));
			} else if (charSelector < 36) {
				sb.append((char) ('A' + (Math.random() * 26)));
			} else {
				sb.append((char) ('a' + (Math.random() * 26)));
			}
		}
		
		return sb.toString();
	}

}
