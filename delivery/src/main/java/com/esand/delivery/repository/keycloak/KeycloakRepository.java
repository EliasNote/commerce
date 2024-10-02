package com.esand.delivery.repository.keycloak;

import com.esand.delivery.entity.KeycloakAccess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeycloakRepository extends JpaRepository<KeycloakAccess, Long>  {
}
