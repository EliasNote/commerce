package com.esand.orders.client;

import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class KeycloakTokenInterceptor implements ClientHttpRequestInterceptor {

    private final Keycloak keycloak;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String token = keycloak.tokenManager().getAccessToken().getTokenType() + " " + keycloak.tokenManager().getAccessToken().getToken();
        request.getHeaders().add(HttpHeaders.AUTHORIZATION, token);
        return execution.execute(request, body);
    }
}
