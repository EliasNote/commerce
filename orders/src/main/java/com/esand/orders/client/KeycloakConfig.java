package com.esand.orders.client;

import com.esand.orders.entity.KeycloakAccess;
import com.esand.orders.service.KeycloakService;
import jakarta.annotation.PostConstruct;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeycloakConfig {

    @Autowired
    private KeycloakService keycloakService;

    private KeycloakAccess keycloakAccess;

    @Value("${keycloak.server-url}")
    private String serverUrl;

    @PostConstruct
    public void init() {
        this.keycloakAccess = keycloakService.getKeycloakAccess();
    }

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(keycloakAccess.getRealm())
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(keycloakAccess.getClientId())
                .clientSecret(keycloakAccess.getClientSecret())
                .username(keycloakAccess.getUsername())
                .password(keycloakAccess.getPassword())
                .build();
    }
}