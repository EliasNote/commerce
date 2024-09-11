package com.esand.orders.client;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;

@Configuration
@Slf4j
public class FeignConfig {

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    @Bean
    public RequestInterceptor requestKeycloakInterceptor(KeycloakTokenProvider keycloakTokenProvider) {
        log.debug("Realizando autenticação keycloak com serviços internos para chamada via Feign Client");

        return requestTemplate -> {
            final var token = keycloakTokenProvider.getToken();
            requestTemplate.header("Authorization", token);
        };
    }
}
