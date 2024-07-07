package com.esand.delivery.web.controller;

import com.esand.delivery.service.DeliveryService;
import com.esand.delivery.web.dto.DeliveryResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/processeds")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping
    public ResponseEntity<List<DeliveryResponseDto>> findAll() {
        return ResponseEntity.ok(deliveryService.findAll());
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<String> cancelProcessed(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.cancel(id));
    }
}
