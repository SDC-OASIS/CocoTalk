package com.cocotalk.presence.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Presence API", version = "1.0", description = "Documentation Presence API v1.0"))
@SecurityScheme(name = "X-ACCESS-TOKEN", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER, bearerFormat = "JWT", scheme = "bearer")
public class SpringDocConfig {
}
