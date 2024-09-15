package com.esand.orders.client;

import com.esand.orders.client.clients.CustomerClient;
import com.esand.orders.client.products.ProductClient;
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

    @Value("${clients.api.url}")
    private String clientsUrl;

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

    @Bean
    public CustomerClient clientClient() {
        Consumer<HttpHeaders> consumer = headers -> headers.add(
                "Authorization",
                keycloak.tokenManager().getAccessToken().getTokenType() + " " + keycloak.tokenManager().getAccessToken().getToken());

        var client = RestClient.builder()
                .baseUrl(clientsUrl)
                .defaultHeaders(consumer)
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(CustomerClient.class);
    }
}
