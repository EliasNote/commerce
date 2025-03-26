package com.esand.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final String[] freeResourceUrls = {
            "/products/docs-ui/**", "/products/docs-api/**",
            "/customers/docs-ui/**", "/customers/docs-api/**",
            "/orders/docs-ui/**", "/orders/docs-api/**",
            "/deliveries/docs-ui/**", "/deliveries/docs-api/**"
    };

    private final String[] adminResourceUrls = {
            "/products/actuator/**", "/products/actuator",
            "/customers/actuator/**", "/customers/actuator",
            "/orders/actuator/**", "/orders/actuator",
            "/deliveries/actuator/**", "/deliveries/actuator"
    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(authorize -> authorize
                        .pathMatchers(freeResourceUrls).permitAll()
                        .pathMatchers(HttpMethod.GET, adminResourceUrls).hasRole("ADMIN")
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults()))
                .build();
    }


    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        var jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakReactiveJwtGrantedAuthoritiesConverter());
        return jwtAuthenticationConverter;
    }
}
