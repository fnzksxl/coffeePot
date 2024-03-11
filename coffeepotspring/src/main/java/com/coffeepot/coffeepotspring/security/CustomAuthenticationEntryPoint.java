package com.coffeepot.coffeepotspring.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import com.coffeepot.coffeepotspring.dto.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		String json = new ObjectMapper().writeValueAsString(ResponseDTO.builder().error(authException.getMessage()).build());
		response.getWriter().write(json);
	}

}
