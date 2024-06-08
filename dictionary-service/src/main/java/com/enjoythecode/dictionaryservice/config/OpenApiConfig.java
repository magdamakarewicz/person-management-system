package com.enjoythecode.dictionaryservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DictionaryServiceApplication")
                        .version("1.0")
                        .description("The application provides a system that stores dictionaries with the values stored in them."));
    }

}
