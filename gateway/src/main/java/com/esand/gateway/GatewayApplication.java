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

				.route(r -> r.path("/products/actuator").filters(f -> f.rewritePath("/products/actuator", "/actuator")).uri("lb://products"))
				.route(r -> r.path("/products/actuator/**").filters(f -> f.rewritePath("/products/actuator/(?<segment>.*)", "/actuator/${segment}")).uri("lb://products"))
				.route(r -> r.path("/customers/actuator").filters(f -> f.rewritePath("/customers/actuator", "/actuator")).uri("lb://customers"))
				.route(r -> r.path("/customers/actuator/**").filters(f -> f.rewritePath("/customers/actuator/(?<segment>.*)", "/actuator/${segment}")).uri("lb://customers"))
				.route(r -> r.path("/orders/actuator").filters(f -> f.rewritePath("/orders/actuator", "/actuator")).uri("lb://orders"))
				.route(r -> r.path("/orders/actuator/**").filters(f -> f.rewritePath("/orders/actuator/(?<segment>.*)", "/actuator/${segment}")).uri("lb://orders"))
				.route(r -> r.path("/deliveries/actuator").filters(f -> f.rewritePath("/deliveries/actuator", "/actuator")).uri("lb://delivery"))
				.route(r -> r.path("/deliveries/actuator/**").filters(f -> f.rewritePath("/deliveries/actuator/(?<segment>.*)", "/actuator/${segment}")).uri("lb://delivery"))

				.route(r -> r.path("/products/docs-ui/**").filters(f -> f.rewritePath("/products/docs-ui/(?<segment>.*)", "/swagger-ui/${segment}")).uri("lb://products"))
				.route(r -> r.path("/products/docs-api").filters(f -> f.rewritePath("/products/docs-api", "/docs-api")).uri("lb://products"))
				.route(r -> r.path("/customers/docs-ui/**").filters(f -> f.rewritePath("/customers/docs-ui/(?<segment>.*)", "/swagger-ui/${segment}")).uri("lb://customers"))
				.route(r -> r.path("/customers/docs-api").filters(f -> f.rewritePath("/customers/docs-api", "/docs-api")).uri("lb://customers"))
				.route(r -> r.path("/orders/docs-ui/**").filters(f -> f.rewritePath("/orders/docs-ui/(?<segment>.*)", "/swagger-ui/${segment}")).uri("lb://orders"))
				.route(r -> r.path("/orders/docs-api").filters(f -> f.rewritePath("/orders/docs-api", "/docs-api")).uri("lb://orders"))
				.route(r -> r.path("/deliveries/docs-ui/**").filters(f -> f.rewritePath("/deliveries/docs-ui/(?<segment>.*)", "/swagger-ui/${segment}")).uri("lb://delivery"))
				.route(r -> r.path("/deliveries/docs-api").filters(f -> f.rewritePath("/deliveries/docs-api", "/docs-api")).uri("lb://delivery"))
				.build();
	}
}
