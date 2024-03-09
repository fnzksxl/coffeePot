package com.coffeepot.coffeepotspring.error;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.coffeepot.coffeepotspring.dto.ResponseDTO;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
			HttpStatusCode statusCode, WebRequest request) {
		ResponseDTO<Object> response = ResponseDTO.builder().error(ex.getMessage()).build();
		return ResponseEntity.status(statusCode.value()).body(response);
	}
	
	@ExceptionHandler
	protected ResponseEntity<?> handleException(Exception e) {
		ResponseDTO<Object> response = ResponseDTO.builder().error(e.getMessage()).build();
		return ResponseEntity.badRequest().body(response);
	}

}
