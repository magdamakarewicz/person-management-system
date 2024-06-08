package com.enjoythecode.userservice.config;

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
                        .title("UserServiceApplication")
                        .version("1.0")
                        .description("The application provides a system that stores data about users " +
                                "and the roles assigned to them."));
    }

}
