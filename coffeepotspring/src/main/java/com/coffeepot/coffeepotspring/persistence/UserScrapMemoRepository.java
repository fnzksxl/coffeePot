package com.coffeepot.coffeepotspring.persistence;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coffeepot.coffeepotspring.model.MemoEntity;
import com.coffeepot.coffeepotspring.model.UserEntity;
import com.coffeepot.coffeepotspring.model.UserScrapMemo;

public interface UserScrapMemoRepository extends JpaRepository<UserScrapMemo, String> {
	
	Optional<UserScrapMemo> findByMemoScraperAndMemoEntity(UserEntity memoScraper, MemoEntity memoEntity);

}
