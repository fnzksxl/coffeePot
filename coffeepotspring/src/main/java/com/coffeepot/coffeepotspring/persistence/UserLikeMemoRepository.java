package com.coffeepot.coffeepotspring.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coffeepot.coffeepotspring.model.MemoEntity;
import com.coffeepot.coffeepotspring.model.UserEntity;
import com.coffeepot.coffeepotspring.model.UserLikeMemo;

public interface UserLikeMemoRepository extends JpaRepository<UserLikeMemo, String> {
	
	Optional<UserLikeMemo> findByMemoLikerAndMemoEntity(UserEntity memoLiker, MemoEntity memoEntity);

}
