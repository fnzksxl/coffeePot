package com.coffeepot.coffeepotspring.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSigninResponseDTO {
	
	private String id;
	private String username;
	private String accessToken;
	private String refreshToken;

}
