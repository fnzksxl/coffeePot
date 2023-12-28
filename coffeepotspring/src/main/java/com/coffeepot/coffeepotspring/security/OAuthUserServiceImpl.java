package com.coffeepot.coffeepotspring.security;

import java.util.Map;
import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.coffeepot.coffeepotspring.model.UserEntity;
import com.coffeepot.coffeepotspring.persistence.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthUserServiceImpl extends DefaultOAuth2UserService {

	private final UserRepository userRepository;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		// super.loadUser()는 user-info-uri를 가지고 사용자 정보 가져옴
		final OAuth2User oAuth2User = super.loadUser(userRequest);

		try {
			// 디버깅 위해 사용자 정보 출력. 테스팅 시에만 써야 함.
			log.info("OAuth2User attributes {}", new ObjectMapper().writeValueAsString(oAuth2User.getAttributes()));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		final String authProvider = userRequest.getClientRegistration().getClientName();
		// username으로 사용할 필드 가져옴
		// authprovider마다 구성이 다름
		final String email, username;
		switch (authProvider) {
		case "GitHub":
			email = "";
			username = (String) oAuth2User.getAttributes().get("login");
			break;
		case "Google":
			email = (String) oAuth2User.getAttributes().get("email");
			username = (String) oAuth2User.getAttributes().get("name");
			break;
		case "Kakao":
			Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
			email = (String) kakaoAccount.get("email");
			Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
			username = (String) profile.get("nickname");
			break;
		case "Naver":
			Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttributes().get("response");
			email = (String) response.get("email");
			username = (String) response.get("name");
			break;
		default:
			email = "";
			username = "";
		}

		Optional<UserEntity> oUserEntity = userRepository.findByEmail(email);
		UserEntity userEntity = null;
		if (oUserEntity.isPresent()) {
			userEntity = oUserEntity.get();
			userEntity.updateEmail(username);
			userEntity = userRepository.save(userEntity);
		} else {
			userEntity = UserEntity.builder()
					.email(email)
					.username(username)
					.authProvider(authProvider)
					.build();
			userEntity = userRepository.save(userEntity); 
		}

		log.info("Successfully pulled user info username {} authProvider {}",
				username,
				authProvider);

		return new ApplicationOAuth2User(userEntity.getId(), oAuth2User.getAttributes());
	}

}
