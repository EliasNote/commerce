package com.esand.orders.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KeycloakTokenProvider {

    private final KeycloakService keycloakService;

    public String getToken() {
        return keycloakService.fetchToken();
    }
}
