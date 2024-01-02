package com.coffeepot.coffeepotspring.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.coffeepot.coffeepotspring.model.UserEntity;
import com.coffeepot.coffeepotspring.persistence.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	
	private final UserRepository userRepository;
	
	public UserEntity create(final UserEntity userEntity) {
		if (userEntity == null || userEntity.getUsername() == null) {
			throw new RuntimeException("Invalid arguments");
		}
		final String username = userEntity.getUsername();
		if (userRepository.existsByUsername(username)) {
			log.warn("Username already exsits {}", username);
			throw new RuntimeException("Username already exists");
		}
		
		return userRepository.save(userEntity);
	}
	
	public UserEntity getByCredentials(final String username,
			final String password, final PasswordEncoder encoder) {
		final UserEntity originalUser = userRepository.findByUsername(username);
		
		// matches 메소드 이용해 패스워드가 같은지 확인
		if (originalUser != null &&
				encoder.matches(password, originalUser.getPassword())) {
			return originalUser;
		}
		return null;
	}
	
	public UserEntity findById(String userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("Unexpected User"));
	}

	// TODO 아이디 찾기
	// 가입 정보 정해지면 구체적으로 구현하기
	// 일단 이메일로 임시 구현
	public UserEntity getByUsernameInfo(List<String> usernameInfo) {
		return userRepository.findByEmail(usernameInfo.get(0))
				.orElseThrow(() -> new IllegalArgumentException("Unexpected Username Info"));
		// 이후 다른 정보로 matches 검사...
	}

	public UserEntity getByPasswordInfo(List<String> passwordInfo) {
		return userRepository.findByEmail(passwordInfo.get(0))
				.orElseThrow(() -> new IllegalArgumentException("Unexpected Password Info"));
		// 이후 다른 정보로 matches 검사...
	}

	public void updatePassword(String username, String password, PasswordEncoder passwordEncoder) {
		UserEntity userEntity = userRepository.findByUsername(username);
		userEntity.updatePassword(password);
		userRepository.save(userEntity);
	}

	public UserEntity updatePassword(UserEntity userEntity) {
		UserEntity originalUser = userRepository.findByUsername(userEntity.getUsername());
		originalUser.updatePassword(userEntity.getPassword());
		return userRepository.save(originalUser);
	}

}
