package com.coffeepot.coffeepotspring.model;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
	
	@Id
	@UuidGenerator
	@Column(updatable = false)
	private String id;
	
	@Column(nullable = false, unique = true)
	private String userId;
	
	@Column(nullable = false)
	private String refreshToken;
	
	public RefreshToken update(String newRefreshToken) {
		this.refreshToken = newRefreshToken;
		return this;
	}

}
