package com.coffeepot.coffeepotspring.security;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.coffeepot.coffeepotspring.model.RefreshToken;
import com.coffeepot.coffeepotspring.model.UserEntity;
import com.coffeepot.coffeepotspring.persistence.JwtRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenProvider {
	
	private final JwtRepository jwtRepository;

	// deprecated
//	private static final String SECRET_KEY = "d29hZXBpdGh1Z2tkbGZobnZqa2x6eGRoMTIzNDgNCjk"
//			+ "wNTY5MDgyMzU3MjM5MOOFkOOFlOOFiOOEt+uogOOFl+OFheOFjuudvOOFo+OFgeOFh+uGh1JJQ"
//			+ "UVPVURHSFRCRkpLU0FETEZIRyYqXigqXiokXiYqKylffToNCns+Ij9+IUAjdyQ=";
	private static final String ISSUER = "coffeepot";
	private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

	public String createAccessToken(UserEntity userEntity) {
		// 기한은 지금부터 1일로 설정
		Date expiryDate = Date.from(
				Instant.now()
				.plus(1, ChronoUnit.HOURS));
		
		/*
		 * { // header
		 *   "alg": "HS512"
		 * }.
		 * { // payload
		 *   "sub": "~~~...",
		 *   "iss": "demo app",
		 *   "iat": "1595733657",
		 *   "exp": "1596597657"
		 * }.
		 * // 서명
		 * ~~~...
		 */
		// JWT Token 생성
		return Jwts.builder()
				// 헤더에 들어갈 내용 및 서명 SECRET_KEY 설정
				// signWith(SignatureAlgorithm, String)은 deprecated
				.signWith(SECRET_KEY, SignatureAlgorithm.HS512)
				// payload에 들어갈 내용
				.setSubject(userEntity.getId()) // sub
				.setIssuer(ISSUER) // iss
				.setIssuedAt(new Date())
				.setExpiration(expiryDate)
				.compact();
	}
	
	public String createAccessToken(final Authentication authentication) {
		ApplicationOAuth2User userPrincipal = (ApplicationOAuth2User) authentication.getPrincipal(); 
		
		// 기한은 지금부터 1일로 설정
		Date expiryDate = Date.from(
				Instant.now()
				.plus(1, ChronoUnit.HOURS));
		
		// JWT Token 생성
		return Jwts.builder()
				// 헤더에 들어갈 내용 및 서명 SECRET_KEY 설정
				// signWith(SignatureAlgorithm, String)은 deprecated
				.signWith(SECRET_KEY, SignatureAlgorithm.HS512)
				// payload에 들어갈 내용
				.setSubject(userPrincipal.getName()) // sub
				.setIssuer(ISSUER) // iss
				.setIssuedAt(new Date())
				.setExpiration(expiryDate)
				.compact();
	}
	
	public String createRefreshToken(UserEntity userEntity) {
		// 기한은 지금부터 2주로 설정
		Date expiryDate = Date.from(
				Instant.now()
				.plus(14, ChronoUnit.DAYS));
		
		String newRefreshToken = Jwts.builder()
				.signWith(SECRET_KEY, SignatureAlgorithm.HS512)
				.setSubject(userEntity.getUsername())
				.setIssuer(ISSUER) // iss
				.setIssuedAt(new Date())
				.setExpiration(expiryDate)
				.compact();
		
		jwtRepository.findByUserId(userEntity.getUsername())
				.ifPresent((refreshToken) -> {
					jwtRepository.delete(refreshToken);
				});
		
		jwtRepository.save(RefreshToken.builder()
				.userId(userEntity.getUsername())
				.refreshToken(newRefreshToken)
				.build());
		
		return newRefreshToken;
	}
	
	public String createRefreshToken(final Authentication authentication) {
		ApplicationOAuth2User userPrincipal = (ApplicationOAuth2User) authentication.getPrincipal();
		
		Date expiryDate = Date.from(
				Instant.now()
				.plus(14, ChronoUnit.DAYS));
		
		String newRefreshToken = Jwts.builder()
				.signWith(SECRET_KEY, SignatureAlgorithm.HS512)
				.setSubject(userPrincipal.getName())
				.setIssuer(ISSUER) // iss
				.setIssuedAt(new Date())
				.setExpiration(expiryDate)
				.compact();
		
		jwtRepository.findByUserId(userPrincipal.getName())
				.ifPresent((refreshToken) -> {
					jwtRepository.delete(refreshToken);
				});
		
		jwtRepository.save(RefreshToken.builder()
				.userId(userPrincipal.getName())
				.refreshToken(newRefreshToken)
				.build());
		
		return newRefreshToken;
	}
	
	public String validateAndGetUserId(String token) {
		Claims claims = Jwts.parserBuilder() // parser()는 deprecated, 그래서 이후 메소드도 수정
				.setSigningKey(SECRET_KEY) // 서명하는 키 설정
				.build()
				.parseClaimsJws(token) // Base 64로 디코딩 및 파싱. 위조됐으면 예외, 아니면 Claims(페이로드) 리턴
				.getBody();
		
		return claims.getSubject();
	}

	// refresh token이 DB에 있는지 검증 후 있으면 새 access token 발급
	public String validateAndReissueAccessToken(String token, UserEntity userEntity) {
		Optional<RefreshToken> refreshToken = jwtRepository.findByRefreshToken(token);
		if (refreshToken.isPresent()) {
			String accessToken = createAccessToken(userEntity);
			return accessToken;
		} else {
		    return null;
		}
	}

}
