package com.coffeepot.coffeepotspring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	private final long MAX_AGE_SECS = 3600;
	private final String RESOURCE_PATH = "/memo-image/**";
	private final String SAVE_PATH = "file:///C:/Users/KWC/Desktop/PKNU/Y2023/CoffePot/coffeePot-BE/coffeepotspring/src/main/resources/memoimages/";

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		// 모든 경로에 대해
		registry.addMapping("/**")
				// Origin이 http://localhost:3000인 경우
				.allowedOrigins("http://localhost:3000")
				// 아래의 메소드들을 허용
				.allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
				.allowedHeaders("*")
				.allowCredentials(true)
				.maxAge(MAX_AGE_SECS);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(RESOURCE_PATH).addResourceLocations(SAVE_PATH);
	}

}
