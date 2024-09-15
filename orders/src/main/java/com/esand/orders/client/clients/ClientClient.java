package com.esand.orders.client.clients;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface ClientClient {

    @GetExchange("/cpf/{cpf}")
    Client getClientByCpf(@PathVariable("cpf") String cpf);
}
