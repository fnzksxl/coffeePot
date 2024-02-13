package com.coffeepot.coffeepotspring.model;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ImageData")
public class ImageDataEntity {
	
	@Id
	@UuidGenerator
	private String id;
	
	@ManyToOne
	private MemoEntity memoEntity; // 이 이미지가 첨부된 메모
	
	private String originalName;
	private String savedName;
	
	private String type;

}
