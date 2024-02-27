package com.coffeepot.coffeepotspring.persistence;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.coffeepot.coffeepotspring.dto.MemoSearchParamDTO;
import com.coffeepot.coffeepotspring.model.MemoEntity;
import com.coffeepot.coffeepotspring.model.QMemoEntity;
import com.coffeepot.coffeepotspring.model.QUserEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@RequiredArgsConstructor
public class MemoRepositoryQuerydslImpl implements MemoRepositoryQuerydsl {
	
	private final JPAQueryFactory jpaQueryFactory;

	/**
	 * 최신 MemoEntity n개 리턴 - 페이징
	 */
	@Override
	public List<MemoEntity> findFirstNBySearchParamOrderByCreatedAtDesc(MemoSearchParamDTO memoSearchParamDTO, long n) {
		QMemoEntity memo = QMemoEntity.memoEntity;
		QUserEntity user = QUserEntity.userEntity;
		
		return jpaQueryFactory
				.selectFrom(memo)
				.leftJoin(user)
				.on(memo.userId.eq(user.id))
				.where(checkAllCond(memoSearchParamDTO))
				.orderBy(memo.createdAt.desc())
				.limit(n)
				.fetch();
	}
	
	/**
	 * 커서(memoId 기준)보다 전에 생성된 MemoEntity를 검색 조건(memoSearchParamDTO)에 맞게 n개 리턴 - 페이징
	 */
	@Override
	public List<MemoEntity> findNBySearchParamOrderByCreatedAtDesc(MemoSearchParamDTO memoSearchParamDTO, String memoId,
			long n) {
		QMemoEntity memo = QMemoEntity.memoEntity;
		QUserEntity user = QUserEntity.userEntity;
		
		MemoEntity memoEntity = jpaQueryFactory
				.selectFrom(memo)
				.where(memo.id.eq(memoId))
				.fetchOne();
		return jpaQueryFactory
				.selectFrom(memo)
				.leftJoin(user)
				.on(memo.userId.eq(user.id))
				.where(checkAllCond(memoSearchParamDTO), memo.createdAt.lt(memoEntity.getCreatedAt()))
				.orderBy(memo.createdAt.desc())
				.limit(n)
				.fetch();
	}

	/**
	 * 검색 조건에 맞는 MemoEntity를 최신순으로 리턴
	 */
	@Override
	public List<MemoEntity> findAllBySearchParamOrderByCreatedAtDesc(MemoSearchParamDTO memoSearchParamDTO) {
		QMemoEntity memo = QMemoEntity.memoEntity;
		QUserEntity user = QUserEntity.userEntity;
		
		return jpaQueryFactory
				.selectFrom(memo)
				.leftJoin(user)
				.on(memo.userId.eq(user.id))
				.where(checkAllCond(memoSearchParamDTO))
				.orderBy(memo.createdAt.desc())
				.fetch();
	}
	
	private BooleanBuilder checkAllCond(MemoSearchParamDTO memoSearchParamDTO) {
		BooleanBuilder builder = new BooleanBuilder();
		return builder
				.and(titleLike(memoSearchParamDTO.getTitleKeyword()))
				.and(contentLike(memoSearchParamDTO.getContentKeyword()))
				.and(authorLike(memoSearchParamDTO.getAuthorKeyword()));
	}
	
	private BooleanExpression titleLike(String title) {
		return StringUtils.hasText(title) ? QMemoEntity.memoEntity.title.containsIgnoreCase(title) : null;
	}
	
	private BooleanExpression contentLike(String content) {
		return StringUtils.hasText(content) ? QMemoEntity.memoEntity.content.containsIgnoreCase(content) : null;
	}
	
	private BooleanExpression authorLike(String author) {
		return StringUtils.hasText(author) ? QUserEntity.userEntity.username.containsIgnoreCase(author) : null;
	}

}
