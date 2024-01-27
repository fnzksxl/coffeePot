package com.coffeepot.coffeepotspring.persistence;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.coffeepot.coffeepotspring.model.MemoEntity;

public interface MemoRepository extends JpaRepository<MemoEntity, String> {
	
	List<MemoEntity> findByUserId(String userId);
	
	Optional<MemoEntity> findFirstByOrderByCreatedAtDesc();
	
	Page<MemoEntity> findAllByCreatedAtLessThanEqualOrderByCreatedAtDesc(LocalDateTime createdAt, Pageable pageable);
	Page<MemoEntity> findAllByCreatedAtLessThanOrderByCreatedAtDesc(LocalDateTime createdAt, Pageable pageable);

}
