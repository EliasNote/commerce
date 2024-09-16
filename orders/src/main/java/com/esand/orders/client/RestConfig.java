package com.esand.orders.client;

import com.esand.orders.client.customers.CustomerClient;
import com.esand.orders.client.products.ProductClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class RestConfig {

    private final KeycloakTokenInterceptor tokenInterceptor;

    @Value("${products.api.url}")
    private String productUrl;

    @Value("${customers.api.url}")
    private String customersUrl;

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
                .baseUrl(customersUrl)
                .requestInterceptor(tokenInterceptor)
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(client);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(CustomerClient.class);
    }
}
