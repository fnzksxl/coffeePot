package com.coffeepot.coffeepotspring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CorsFilter;

import com.coffeepot.coffeepotspring.security.CustomAuthenticationEntryPoint;
import com.coffeepot.coffeepotspring.security.JwtAuthenticationFilter;
import com.coffeepot.coffeepotspring.security.OAuthAuthorizationRequestBasedOnCookieRepository;
import com.coffeepot.coffeepotspring.security.OAuthSuccessHandler;
import com.coffeepot.coffeepotspring.security.OAuthUserServiceImpl;
import com.coffeepot.coffeepotspring.security.RedirectUrlCookieFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final OAuthUserServiceImpl oAuthUserService;
	private final OAuthSuccessHandler oAuthSuccessHandler;
	private final RedirectUrlCookieFilter redirectUrlFilter;
	private final OAuthAuthorizationRequestBasedOnCookieRepository oAuthAuthorizationRequestBasedOnCookieRepository;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// http 시큐리티 빌더
		http
		.cors(Customizer.withDefaults()) // WebMvcConfig에서 이미 설정했으므로 기본 cors 설정
		.csrf((csrf) -> csrf
				.disable()) // csrf는 현재 사용하지 않으므로 disable
		.httpBasic((httpBasic) -> httpBasic
				.disable()) // token 방식 사용하므로 basic 방식 disable
		.sessionManagement((management) -> management // 세션 기반이 아님을 선언
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.authorizeHttpRequests((requests) -> requests
				// antMatchers는 deprecated돼서 아래 사용
				.requestMatchers(
						new AntPathRequestMatcher("/"), // 이 경로들은 인증 안 해도 됨
						new AntPathRequestMatcher("/auth/**"),
						new AntPathRequestMatcher("/oauth2/**"),
						new AntPathRequestMatcher("/memo/page"),
						new AntPathRequestMatcher("/search/**"),
						new AntPathRequestMatcher("/swagger-ui/**"),
						new AntPathRequestMatcher("/v3/api-docs/**"))
				.permitAll()
				.anyRequest() // 그 외의 경로는 모두 인증해야 됨
				.authenticated())
		.oauth2Login((oauth2) -> oauth2 // oauth2 로그인 설정
				.redirectionEndpoint((endpoint) -> endpoint
						.baseUri("/oauth2/callback/*"))
				.authorizationEndpoint((endpoint) -> endpoint
						.baseUri("/auth/authorize")
						.authorizationRequestRepository(oAuthAuthorizationRequestBasedOnCookieRepository)) // Spring boot의 기본값인 /oauth2/authorization을 /auth/authorization으로 변경
				.userInfoEndpoint((endpoint) -> endpoint
						.userService(oAuthUserService)) // callback uri 설정, oauth2Login 설정
				.successHandler(oAuthSuccessHandler))
		.exceptionHandling((exception) -> exception
				// authenticationEntryPoint: 인증의 시작점
				.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));
		// TODO
		// redirect_url을 파라미터로 넘겨주면 클라이언트에서 아무나 이 값을 바꿀 수 있으므로
		// 실제 서비스에서는 서버에서 redirect_url이 허용된 도메인을 갖고 있는지 확인해야 한다.

		// 매 요청마다 CorsFilter 실행 후 jwtAuthenticationFilter 실행
		http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);
		// 리디렉트 전에 필터 실행
		http.addFilterBefore(redirectUrlFilter, OAuth2AuthorizationRequestRedirectFilter.class);

		return http.build();
	}

}
