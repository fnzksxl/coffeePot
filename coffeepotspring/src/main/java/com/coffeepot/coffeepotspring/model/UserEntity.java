package com.coffeepot.coffeepotspring.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = "username") })
public class UserEntity {

	@Id
	@UuidGenerator
	private String id;

	private String email;

	@Column(nullable = false)
	private String username;

	// null을 허용 안 하면 OAuth로 SSO 구현 시 문제가 생기므로 null 허용
	// 여기서는 허용하되 회원가입 구현 시 허용하지 않으면 된다.
	private String password;
	private String role; // 사용자의 롤 (애드민, 일반 등)
	private String authProvider; // 이후 OAuth에서 사용할 유저 정보 제공자 github 등

	@Builder.Default
	@OneToMany(mappedBy = "userId", cascade = CascadeType.REMOVE)
	private List<MemoEntity> writtenMemos = new ArrayList<>();

	// 다대다 관계는 명시적 테이블 생성
	// users - scrap - memos
	// users - like - memos
	// users - like - cafes
	@Builder.Default
	@OneToMany(mappedBy = "memoScraper")
	private List<UserScrapMemo> scrapedMemos = new ArrayList<>();

	@Builder.Default
	@OneToMany(mappedBy = "memoLiker")
	private List<UserLikeMemo> likedMemos = new ArrayList<>();

	public void updateEmail(String email) {
		this.email = email;
	}

	public void updatePassword(String password) {
		this.password = password;
	}

}
