package com.esand.orders.client.products;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "products", url = "${products.api.url}")
public interface ProductClient {

    @GetMapping("/title/{title}")
    Product getProductByTitle(@PathVariable String title);

    @PatchMapping("/sku/{sku}/sub/{quantity}")
    void decreaseProductQuantityBySku(@PathVariable String sku, @PathVariable Integer quantity);
}
