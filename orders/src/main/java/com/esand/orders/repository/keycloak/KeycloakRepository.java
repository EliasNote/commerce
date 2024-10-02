package com.esand.orders.repository.keycloak;

import com.esand.orders.entity.KeycloakAccess;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeycloakRepository extends JpaRepository<KeycloakAccess, Long>  {
}
