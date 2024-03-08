package com.coffeepot.coffeepotspring.dto;

import java.util.List;

import com.coffeepot.coffeepotspring.model.HashTagEntity;
import com.coffeepot.coffeepotspring.model.ImageDataEntity;
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
	
	public static MemoResponseDTO of(final MemoEntity memoEntity, final List<HashTagEntity> hashTagEntities,
			final List<ImageDataEntity> imageDataEntities) {
		return MemoResponseDTO.builder()
				.id(memoEntity.getId())
				.title(memoEntity.getTitle())
				.content(memoEntity.getContent())
				.visibility(memoEntity.getVisibility() ? "public" : "private")
				.createdAt(memoEntity.getCreatedAt().toString())
				.updatedAt((memoEntity.getUpdatedAt() == null) ? null : memoEntity.getUpdatedAt().toString())
				.likeCount(memoEntity.getLikeCount())
				.scrapCount(memoEntity.getScrapCount())
				.hashTags((hashTagEntities == null) ? null : hashTagEntities.stream().map(h -> h.getHashTag()).toList())
				.imagesUris((imageDataEntities == null) ? null : imageDataEntities.stream().map(i -> i.getSavedName()).toList())
				.build();
	}

}