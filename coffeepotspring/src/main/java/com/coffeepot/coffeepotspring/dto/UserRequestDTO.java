package com.coffeepot.coffeepotspring.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRequestDTO {
	
	private String username;
	private String password;
	private String email;

}
