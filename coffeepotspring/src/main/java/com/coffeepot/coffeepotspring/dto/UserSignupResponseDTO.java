package com.coffeepot.coffeepotspring.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSignupResponseDTO {
	
	private String id;
	private String username;

}
