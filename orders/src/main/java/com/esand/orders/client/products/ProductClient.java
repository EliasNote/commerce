package com.esand.orders.client.products;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "products", url = "${products.api.url}")
public interface ProductClient {

    @GetMapping("/sku/{sku}")
    Product getProductBySku(@PathVariable String sku);

    @PatchMapping("/sku/{sku}/sub/{quantity}")
    void decreaseProductQuantityBySku(@PathVariable String sku, @PathVariable Integer quantity);
}
