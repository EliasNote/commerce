package com.esand.delivery.client.products;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;

@HttpExchange
public interface ProductClient {

    @GetExchange
    void checkStatus();

    @PatchExchange("/sku/{sku}/add/{quantity}")
    void addProductQuantityBySku(@PathVariable String sku, @PathVariable Integer quantity);
}
