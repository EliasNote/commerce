package com.esand.delivery.service;

import com.esand.delivery.entity.KeycloakAccess;
import com.esand.delivery.exception.EntityNotFoundException;
import com.esand.delivery.repository.keycloak.KeycloakRepository;
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
