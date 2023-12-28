package com.coffeepot.coffeepotspring.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coffeepot.coffeepotspring.model.RefreshToken;

@Repository
public interface JwtRepository extends JpaRepository<RefreshToken, String> {
	
	public RefreshToken findByToken(String token);

}
