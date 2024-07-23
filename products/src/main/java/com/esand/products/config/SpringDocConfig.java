package com.esand.products.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("PRODUCTS API")
                                .description("API for product management")
                                .version("v1")
                                .contact(new Contact().name("Elias Mathias Sand").email("elias.coder1@gmail.com.br"))
                );
    }
}