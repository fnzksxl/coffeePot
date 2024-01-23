package com.coffeepot.coffeepotspring.model;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "hash_tag")
public class HashTagEntity {
	
	@Id
	@UuidGenerator
	private String id;
	
	private String hashTag;
	
	@ManyToOne
	private MemoEntity memoEntity;

}
