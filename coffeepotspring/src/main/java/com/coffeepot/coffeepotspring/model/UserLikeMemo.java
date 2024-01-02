package com.coffeepot.coffeepotspring.model;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLikeMemo {

	@Id
	@UuidGenerator
	private String id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserEntity memoLiker;

	@ManyToOne
	@JoinColumn(name = "memo_id")
	private MemoEntity memoEntity;

}
