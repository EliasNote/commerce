package com.esand.orders.client.clients;

import com.esand.orders.client.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "clients", url = "${clients.api.url}", configuration = FeignConfig.class)
public interface ClientClient {

    @GetMapping("/cpf/{cpf}")
    Client getClientByCpf(@PathVariable("cpf") String cpf);
}
