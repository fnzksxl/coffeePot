package com.coffeepot.coffeepotspring.dto;

import com.coffeepot.coffeepotspring.model.MemoEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemoDTO {
	
	private String id;
	private String title;
	private String content;
	private boolean visibility;
	
	public MemoDTO(final MemoEntity memoEntity) {
		this.id = memoEntity.getId();
		this.title = memoEntity.getTitle();
		this.content = memoEntity.getContent();
		this.visibility = memoEntity.getVisibility();
	}
	
	public static MemoEntity toEntity(final MemoDTO memoDTO) {
		return MemoEntity.builder()
				.id(memoDTO.getId())
				.title(memoDTO.getTitle())
				.content(memoDTO.getContent())
				.visibility(memoDTO.isVisibility())
				.build();
	}

}