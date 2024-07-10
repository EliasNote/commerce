package com.esand.delivery.web.controller;

import com.esand.delivery.service.DeliveryService;
import com.esand.delivery.web.dto.DeliveryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping
    public ResponseEntity<List<DeliveryResponseDto>> findAll() {
        return ResponseEntity.ok(deliveryService.findAll());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<DeliveryResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.findById(id));
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<String> cancelDelivery(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.cancel(id));
    }

    @PatchMapping("/status/{id}")
    public ResponseEntity<String> productShipped(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.statusShipped(id));
    }

    @DeleteMapping("/delete/canceled")
    public ResponseEntity<String> deleteAllCanceled() {
        deliveryService.deleteAllCanceled();
        return ResponseEntity.noContent().build();
    }
}
