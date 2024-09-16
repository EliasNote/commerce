package com.esand.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

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
				.route(r -> r.path("/api/v1/customers/**").uri("lb://customers"))
				.route(r -> r.path("/api/v1/orders/**").uri("lb://orders"))
				.route(r -> r.path("/api/v1/deliveries/**").uri("lb://delivery"))

				.route(r -> r.path("/actuator/products")
						.filters(f -> f.rewritePath("/actuator/products", "/actuator"))
						.uri("lb://products"))
				.route(r -> r.path("/actuator/products/**")
						.filters(f -> f.rewritePath("/actuator/products/(?<segment>.*)", "/actuator/${segment}"))
						.uri("lb://products"))

				.route(r -> r.path("/actuator/customers")
						.filters(f -> f.rewritePath("/actuator/customers", "/actuator"))
						.uri("lb://customers"))
				.route(r -> r.path("/actuator/customers/**")
						.filters(f -> f.rewritePath("/actuator/customers/(?<segment>.*)", "/actuator/${segment}"))
						.uri("lb://customers"))

				.route(r -> r.path("/actuator/orders")
						.filters(f -> f.rewritePath("/actuator/orders", "/actuator"))
						.uri("lb://orders"))
				.route(r -> r.path("/actuator/orders/**")
						.filters(f -> f.rewritePath("/actuator/orders/(?<segment>.*)", "/actuator/${segment}"))
						.uri("lb://orders"))

				.route(r -> r.path("/actuator/deliveries")
						.filters(f -> f.rewritePath("/actuator/deliveries", "/actuator"))
						.uri("lb://delivery"))
				.route(r -> r.path("/actuator/deliveries/**")
						.filters(f -> f.rewritePath("/actuator/deliveries/(?<segment>.*)", "/actuator/${segment}"))
						.uri("lb://delivery"))
				.build();
	}
}
