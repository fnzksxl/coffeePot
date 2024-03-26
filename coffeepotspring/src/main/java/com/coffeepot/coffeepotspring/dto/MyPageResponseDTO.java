package com.coffeepot.coffeepotspring.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MyPageResponseDTO {
	
	private String id;
	private String username;
	private String email;

}
