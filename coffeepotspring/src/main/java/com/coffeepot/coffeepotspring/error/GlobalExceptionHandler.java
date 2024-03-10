package com.coffeepot.coffeepotspring.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.coffeepot.coffeepotspring.dto.ResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	private ResponseEntity<Object> makeResponse(int status, Exception e) {
		ResponseDTO<Object> response = ResponseDTO.builder().error(e.getMessage()).build();
		return ResponseEntity.status(status).body(response);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatusCode statusCode, WebRequest request) {
		return makeResponse(statusCode.value(), ex);
	}
	
	@ExceptionHandler
	public ResponseEntity<?> handleException(Exception e) {
		return makeResponse(HttpStatus.BAD_REQUEST.value(), e);
	}
	
	@ExceptionHandler
	public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
		return makeResponse(HttpStatus.UNAUTHORIZED.value(), e);
	}

}
