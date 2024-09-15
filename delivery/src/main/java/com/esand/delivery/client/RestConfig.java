package com.esand.delivery.client;

import com.esand.delivery.client.products.ProductClient;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class RestConfig {

    private final Keycloak keycloak;

    @Value("${products.api.url}")
    private String productUrl;

    @Bean
    public ProductClient productClient() {
        Consumer<HttpHeaders> consumer = headers -> headers.add(
                "Authorization",
                keycloak.tokenManager().getAccessToken().getTokenType() + " " + keycloak.tokenManager().getAccessToken().getToken());

        var client = RestClient.builder()
                .baseUrl(productUrl)
                .defaultHeaders(consumer)
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(ProductClient.class);
    }
}