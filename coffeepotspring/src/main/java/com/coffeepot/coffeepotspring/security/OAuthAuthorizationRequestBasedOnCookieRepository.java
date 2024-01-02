package com.coffeepot.coffeepotspring.security;

import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.coffeepot.coffeepotspring.util.CookieUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/*
 * OAuth2에 필요한 정보를 세션이 아닌 쿠키에 저장해서 쓸 수 있도록
 * 인증 요청 관련 상태를 저장하는 저장소.
 * 권한 인증 흐름에서 클라이언트의 요청을 유지하는 데 사용하는
 * AuthorizationRequestRepository를 구현하여
 * 쿠키를 통해 OAuth 저옵를 가져오고 저장하는 로직 구현.
 */
@Component
public class OAuthAuthorizationRequestBasedOnCookieRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
	
	public final static String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
	private final static int COOKIE_EXPIRATION_SECONDS = 18000;

	@Override
	public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
		return CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class);
	}

	@Override
	public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request,
			HttpServletResponse response) {
		if (authorizationRequest == null) {
			removeAuthorizationRequest(request, response);
			return;
		}
		CookieUtil.addCookie(
				response,
				OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME,
				CookieUtil.serialize(authorizationRequest),
				COOKIE_EXPIRATION_SECONDS);
	}

	@Override
	public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request,
			HttpServletResponse response) {
		return this.loadAuthorizationRequest(request);
	}
	
	public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
		CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
	}

}
