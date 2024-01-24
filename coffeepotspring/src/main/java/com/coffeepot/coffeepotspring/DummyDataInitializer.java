package com.coffeepot.coffeepotspring;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.coffeepot.coffeepotspring.model.MemoEntity;
import com.coffeepot.coffeepotspring.persistence.MemoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class DummyDataInitializer {
	
	MemoRepository memoRepository;
	
	@EventListener(ApplicationReadyEvent.class)
	public void initDummyData() {
		log.info("Initializing dummy data...");
		List<MemoEntity> memoEntities = new ArrayList<>();
		for (int i = 1; i <= 50; i++) {
			memoEntities.add(MemoEntity.builder()
					.title("test" + i)
					.content("test" + i)
					.visibility(i % 2 == 0)
					.createdAt(LocalDateTime.now())
					.build());
		}
		memoRepository.saveAll(memoEntities);
		log.info("Initializing dummy data is finished");
	}

}
