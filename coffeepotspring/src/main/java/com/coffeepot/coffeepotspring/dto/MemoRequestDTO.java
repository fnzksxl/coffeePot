package com.coffeepot.coffeepotspring.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemoRequestDTO {
	
	private String id;
	private String title;
	private String content;
	private String visibility;
	private List<String> hashTags;
	private List<MultipartFile> uploadedImages;

}
