package com.esand.delivery.client;

import com.esand.delivery.client.customers.CustomerClient;
import com.esand.delivery.client.products.ProductClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class RestConfig {

    private final KeycloakTokenInterceptor tokenInterceptor;

    @Value("${products.api.url}")
    private String productUrl;

    @Value("${customers.api.url}")
    private String customerUrl;

    @Bean
    public ProductClient productClient() {
        var client = RestClient.builder()
                .baseUrl(productUrl)
                .requestInterceptor(tokenInterceptor)
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(ProductClient.class);
    }

    @Bean
    public CustomerClient customerClient() {
        var client = RestClient.builder()
                .baseUrl(customerUrl)
                .requestInterceptor(tokenInterceptor)
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(CustomerClient.class);
    }
}