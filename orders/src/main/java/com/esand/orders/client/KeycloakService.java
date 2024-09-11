package com.esand.orders.client;

import com.google.gson.Gson;
import jakarta.ws.rs.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

import static java.lang.String.format;

@Service
@Slf4j
public class KeycloakService {

    private static final String GRANT_TYPE = "grant_type";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String BEARER = "Bearer ";

    @Value("${keycloak.token.url}")
    private String uri;

    private final Gson gson;
    private final HttpClient client;
    private String cachedToken;
    private Instant tokenExpirationTime;

    public KeycloakService(Gson gson, HttpClient client) {
        this.gson = gson;
        this.client = client;
    }

    public String fetchToken() {
        if (cachedToken != null && Instant.now().isBefore(tokenExpirationTime)) {
            log.info("Usando token em cache");
            return format("%s%s", BEARER, cachedToken);
        }

        log.debug("Executando autenticação");

        var urlParameters = format(
                "%s=%s&%s=%s&%s=%s&%s=%s&%s=%s",
                GRANT_TYPE, "password",
                CLIENT_ID, "gateway",
                CLIENT_SECRET, "ouQ51dghJBhO9UIS3WGC1Mz7GRpMK5Tj",
                USERNAME, "auth",
                PASSWORD, "123"
        );

        HttpResponse<String> response;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8081/realms/commerce/protocol/openid-connect/token"))
                    .headers("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(urlParameters))
                    .build();
            response = client.send(request, HttpResponse.BodyHandlers.ofString());

        } catch (InterruptedException e) {
            log.warn("Erro ao retornar token de acesso via Keycloak. Thread interrompida.");
            Thread.currentThread().interrupt();
            throw new ServiceUnavailableException();
        } catch (Exception e) {
            log.warn("Erro ao retornar token de acesso via Keycloak.");
            throw new ServiceUnavailableException();
        }
        
        KeycloakToken token = gson.fromJson(response.body(), KeycloakToken.class);
        cachedToken = token.getAccess_token();
        tokenExpirationTime = Instant.now().plusSeconds(token.getExpires_in());

        log.info("Token de acesso recebido: {}", token.getAccess_token());
        log.info("Tempo de expiração do token: {} segundos", token.getExpires_in());

        return format("%s%s", BEARER, token.getAccess_token());
    }
}