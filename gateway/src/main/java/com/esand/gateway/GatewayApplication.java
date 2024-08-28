package com.esand.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator routes(RouteLocatorBuilder builder) {
		return builder
				.routes()
				.route(r -> r.path("/api/v1/products/**").uri("lb://products"))
				.route(r -> r.path("/api/v1/clients/**").uri("lb://clients"))
				.route(r -> r.path("/api/v1/orders/**").uri("lb://orders"))
				.route(r -> r.path("/api/v1/deliveries/**").uri("lb://delivery"))
				.build();
	}

	@Bean
	public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		http
				.authorizeExchange(exchanges -> exchanges
						.pathMatchers("/api/**").authenticated()
						.anyExchange().permitAll()
				)
				.oauth2ResourceServer((oauth2) -> oauth2
						.jwt((Customizer.withDefaults()))
				);
		return http.build();
	}
}
