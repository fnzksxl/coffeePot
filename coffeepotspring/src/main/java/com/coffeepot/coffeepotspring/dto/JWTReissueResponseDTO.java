package com.coffeepot.coffeepotspring.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JWTReissueResponseDTO {
	
	private String id;
	private String username;
	private String accessToken;

}
