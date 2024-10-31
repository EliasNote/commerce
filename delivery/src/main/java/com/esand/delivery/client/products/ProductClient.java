package com.esand.delivery.client.products;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;

@HttpExchange
public interface ProductClient {

    @GetExchange
    void checkStatus();

    @PatchExchange("/edit/{sku}")
    void addProductQuantityBySku(@PathVariable String sku, @RequestParam Integer addQuantity);

    @GetExchange("/sku/{sku}")
    Product getProductBySku(@PathVariable String sku);
}
