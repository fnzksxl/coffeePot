package com.coffeepot.coffeepotspring.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemoSearchParamDTO {
	
	private String titleKeyword;
	private String contentKeyword;
	private String authorKeyword;

}
