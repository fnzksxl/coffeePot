package com.coffeepot.coffeepotspring.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.coffeepot.coffeepotspring.dto.MemoSearchParamDTO;
import com.coffeepot.coffeepotspring.model.MemoEntity;

@Repository
public interface MemoRepositoryQuerydsl {
	
	public List<MemoEntity> findFirstNBySearchParamOrderByCreatedAtDesc(MemoSearchParamDTO memoSearchParamDTO, long n);

	public List<MemoEntity> findNBySearchParamOrderByCreatedAtDesc(MemoSearchParamDTO memoSearchParamDTO, String memoId, long n);

	public List<MemoEntity> findAllBySearchParamOrderByCreatedAtDesc(MemoSearchParamDTO memoSearchParamDTO);

}
