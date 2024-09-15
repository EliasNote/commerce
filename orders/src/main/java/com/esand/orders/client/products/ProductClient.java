package com.esand.orders.client.products;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;

@HttpExchange
public interface ProductClient {

    @GetExchange("/sku/{sku}")
    Product getProductBySku(@PathVariable String sku);

    @PatchExchange("/sku/{sku}/sub/{quantity}")
    void decreaseProductQuantityBySku(@PathVariable String sku, @PathVariable Integer quantity);
}
