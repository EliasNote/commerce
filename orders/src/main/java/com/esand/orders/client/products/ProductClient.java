package com.esand.orders.client.products;

import com.esand.orders.client.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "products", url = "${products.api.url}", configuration = FeignConfig.class)
public interface ProductClient {

    @GetMapping("/sku/{sku}")
    Product getProductBySku(@PathVariable String sku);

    @PatchMapping("/sku/{sku}/sub/{quantity}")
    void decreaseProductQuantityBySku(@PathVariable String sku, @PathVariable Integer quantity);
}
