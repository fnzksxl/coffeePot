package com.coffeepot.coffeepotspring.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coffeepot.coffeepotspring.model.RefreshToken;

@Repository
public interface JwtRepository extends JpaRepository<RefreshToken, String> {

	public Optional<RefreshToken> findByRefreshToken(String token);
	public Optional<RefreshToken> findByUserId(String userId);

}
