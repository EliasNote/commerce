package com.esand.delivery.client.products;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "products", url = "${products.api.url}")
public interface ProductClient {

    @GetMapping
    void checkStatus();

    @PatchMapping("/sku/{sku}/add/{quantity}")
    void addProductQuantityBySku(@PathVariable String sku, @PathVariable Integer quantity);
}
