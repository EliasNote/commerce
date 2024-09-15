package com.esand.orders.client.customers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface CustomerClient {

    @GetExchange("/cpf/{cpf}")
    Customer getCustomerByCpf(@PathVariable("cpf") String cpf);
}
