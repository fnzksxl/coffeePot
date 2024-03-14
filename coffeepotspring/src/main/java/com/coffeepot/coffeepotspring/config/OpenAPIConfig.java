package com.coffeepot.coffeepotspring.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;

@Configuration
@SecuritySchemes({
		@SecurityScheme(
				name = "Reissue Authentication",
				type = SecuritySchemeType.HTTP,
				bearerFormat = "JWT",
				scheme = "bearer")
})
public class OpenAPIConfig {

}
