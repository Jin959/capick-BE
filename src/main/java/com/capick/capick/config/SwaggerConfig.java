package com.capick.capick.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(title = "까픽 API",
                description = "까픽 API 명세서",
                version = "v1.0.0")
)
@Configuration
public class SwaggerConfig {
    
}
