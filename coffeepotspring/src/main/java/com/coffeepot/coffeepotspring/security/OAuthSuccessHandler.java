package com.coffeepot.coffeepotspring.security;

import static com.coffeepot.coffeepotspring.security.RedirectUrlCookieFilter.REDIRECT_URI_PARAM;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.coffeepot.coffeepotspring.util.CookieUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class OAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	
	private static final String LOCAL_REDIRECT_URL = "http://localhost:3000";
	private static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
	
	private final OAuthAuthorizationRequestBasedOnCookieRepository oAuthAutorizationRequestBasedOnCookieRepository;
	private final TokenProvider tokenProvider;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		log.info("auth succeeded");
		String accessToken = tokenProvider.createAccessToken(authentication);
		String refreshToken = tokenProvider.createRefreshToken(authentication);
		addRefreshTokenToCookie(request, response, refreshToken);
		
		// 쿠키에 redirect url 있으면 해당 url로 리디렉션하고
		// 없으면 LOCAL_REDIRECT_URL로 리디렉션함
		// 그리고 access token을 uri 파라미터에 추가
		Optional<Cookie> oCookie = Arrays.stream(request.getCookies())
				.filter(cookie -> cookie.getName().equals(REDIRECT_URI_PARAM)).findFirst();
		Optional<String> redirectUri = oCookie.map(Cookie::getValue);
//		response.getWriter().write(token);
		log.info("accessToken {}, refreshToken {}", accessToken);
//		response.sendRedirect("http://localhost:3000/sociallogin?token=" + token);
		response.sendRedirect(redirectUri.orElseGet(() -> LOCAL_REDIRECT_URL)
				+ "/sociallogin?token=" + accessToken);
		
		clearAuthenticationAttributes(request, response);
	}

	private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
		int cookieMaxAge = (int) Duration.ofDays(14).getSeconds();
		CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
		CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
	}
	
	private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
		super.clearAuthenticationAttributes(request);
		oAuthAutorizationRequestBasedOnCookieRepository.removeAuthorizationRequestCookies(request, response);
	}

}
