package com.esand.orders.service;

import com.esand.orders.entity.KeycloakAccess;
import com.esand.orders.exception.EntityNotFoundException;
import com.esand.orders.repository.keycloak.KeycloakRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final KeycloakRepository keycloakRepository;

    public KeycloakAccess getKeycloakAccess() {
        return keycloakRepository.findById(1L).orElseThrow(
                () -> new EntityNotFoundException("Keycloak access not found")
        );
    }
}
