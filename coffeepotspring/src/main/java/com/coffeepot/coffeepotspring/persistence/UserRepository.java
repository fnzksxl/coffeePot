package com.coffeepot.coffeepotspring.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coffeepot.coffeepotspring.model.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

	Optional<UserEntity> findByUsername(String username);
	
	Boolean existsByEmail(String email);

	Boolean existsByUsername(String username);
	
	Optional<UserEntity> findByEmail(String email);

	UserEntity findByUsernameAndPassword(String username, String password);

}
