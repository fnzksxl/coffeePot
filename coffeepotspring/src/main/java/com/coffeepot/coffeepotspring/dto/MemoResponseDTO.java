package com.coffeepot.coffeepotspring.dto;

import java.util.List;

import com.coffeepot.coffeepotspring.model.MemoEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemoResponseDTO {
	
	private String id;
	private String title;
	private String content;
	private String visibility; // 메모 공개 범위 public, private
	private String createdAt;
	private String updatedAt;
	private int likeCount;
	private int scrapCount;
	private List<String> hashTags;
	private List<String> imagesUris;
	
	public MemoResponseDTO(final MemoEntity memoEntity, final List<String> hashTags, final List<String> imagesUri) {
		this.id = memoEntity.getId();
		this.title = memoEntity.getTitle();
		this.content = memoEntity.getContent();
		this.visibility = memoEntity.getVisibility() ? "public" : "private";
		this.createdAt = memoEntity.getCreatedAt().toString();
		if (memoEntity.getUpdatedAt() != null) {
			this.updatedAt = memoEntity.getUpdatedAt().toString();
		}
		this.hashTags = hashTags;
		this.imagesUris = imagesUri;
		this.likeCount = memoEntity.getLikeCount();
		this.scrapCount = memoEntity.getScrapCount();
	}

}