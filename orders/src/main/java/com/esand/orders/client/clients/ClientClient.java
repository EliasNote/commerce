package com.esand.orders.client.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "clients", url = "${clients.api.url}")
public interface ClientClient {

    @GetMapping("/name/{name}")
    Client getClientByName(@PathVariable String name);
}
