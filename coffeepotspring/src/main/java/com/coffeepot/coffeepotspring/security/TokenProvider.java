package com.coffeepot.coffeepotspring.security;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.coffeepot.coffeepotspring.model.UserEntity;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TokenProvider {

	// deprecated
//	private static final String SECRET_KEY = "d29hZXBpdGh1Z2tkbGZobnZqa2x6eGRoMTIzNDgNCjk"
//			+ "wNTY5MDgyMzU3MjM5MOOFkOOFlOOFiOOEt+uogOOFl+OFheOFjuudvOOFo+OFgeOFh+uGh1JJQ"
//			+ "UVPVURHSFRCRkpLU0FETEZIRyYqXigqXiokXiYqKylffToNCns+Ij9+IUAjdyQ=";
	private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);

	public String create(UserEntity userEntity) {
		// 기한은 지금부터 1일로 설정
		Date expiryDate = Date.from(
				Instant.now()
				.plus(1, ChronoUnit.DAYS));
		
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
				.setIssuer("todo app") // iss
				.setIssuedAt(new Date())
				.setExpiration(expiryDate)
				.compact();
	}
	
	public String create(final Authentication authentication) {
		ApplicationOAuth2User userPrincipal = (ApplicationOAuth2User) authentication.getPrincipal(); 
		
		// 기한은 지금부터 1일로 설정
		Date expiryDate = Date.from(
				Instant.now()
				.plus(1, ChronoUnit.DAYS));
		
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
				.setSubject(userPrincipal.getName()) // sub
				.setIssuer("todo app") // iss
				.setIssuedAt(new Date())
				.setExpiration(expiryDate)
				.compact();
	}
	
	public String validateAndGetUserId(String token) {
		Claims claims = Jwts.parserBuilder() // parser()는 deprecated, 그래서 이후 메소드도 수정
				.setSigningKey(SECRET_KEY) // 서명하는 키 설정
				.build()
				.parseClaimsJws(token) // Base 64로 디코딩 및 파싱. 위조됐으면 예외, 아니면 Claims(페이로드) 리턴
				.getBody();
		
		return claims.getSubject();
	}

}
