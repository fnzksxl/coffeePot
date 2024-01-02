package com.coffeepot.coffeepotspring.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.coffeepot.coffeepotspring.model.MemoEntity;

public interface MemoRepository extends JpaRepository<MemoEntity, String> {

}
