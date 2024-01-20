package com.coffeepot.coffeepotspring.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.coffeepot.coffeepotspring.model.ImageDataEntity;
import com.coffeepot.coffeepotspring.model.MemoEntity;
import com.coffeepot.coffeepotspring.persistence.ImageRepository;
import com.coffeepot.coffeepotspring.util.ImageUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
	
	private final ImageRepository imageRepository;

	private final String MEMO_IMAGE_BASE_PATH = "C:/Users/KWC/Desktop/PKNU/Y2023/CoffePot/coffeePot-BE/coffeepotspring/src/main/resources/memoimages/";

	public List<ImageDataEntity> uploadImages(List<ImageDataEntity> imageDataEntities) {
		return imageRepository.saveAll(imageDataEntities);
	}

	public List<String> retrieveSavedNamesByMemoEntity(MemoEntity memoEntity) {
		List<ImageDataEntity> imageDataEntities = imageRepository.findAllByMemoEntity(memoEntity);
		return imageDataEntities.stream().map(image -> image.getSavedName()).toList();
	}

	public void deleteByMemoEntity(MemoEntity memoEntity) {
		List<ImageDataEntity> imageDataEntities = imageRepository.findAllByMemoEntity(memoEntity);
		imageRepository.deleteAll(imageDataEntities);
		for (ImageDataEntity imageDataEntity : imageDataEntities) {
			File file = new File(MEMO_IMAGE_BASE_PATH + imageDataEntity.getSavedName());
			if (file.exists()) {
				if (file.delete()) {
					log.info("파일 삭제 완료: {}" , imageDataEntity.getSavedName());
				} else {
					log.info("파일 삭제 실패: {}" , imageDataEntity.getSavedName());
				}
			} else {
				log.info("파일이 존재하지 않음: {}", imageDataEntity.getSavedName());
			}
		}
	}

}
