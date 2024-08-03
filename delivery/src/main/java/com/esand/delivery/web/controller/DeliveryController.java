package com.esand.delivery.web.controller;

import com.esand.delivery.service.DeliveryService;
import com.esand.delivery.springdoc.SpringDoc;
import com.esand.delivery.web.dto.DeliveryResponseDto;
import com.esand.delivery.web.dto.PageableDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/deliveries")
public class DeliveryController implements SpringDoc {

    private final DeliveryService deliveryService;

    @Override
    @GetMapping
    public ResponseEntity<PageableDto> findAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(deliveryService.findAll(pageable));
    }

    @Override
    @GetMapping("/id/{id}")
    public ResponseEntity<DeliveryResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.findById(id));
    }

    @Override
    @PatchMapping("/cancel/{id}")
    public ResponseEntity<String> cancelDelivery(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.cancel(id));
    }

    @Override
    @PatchMapping("/status/{id}")
    public ResponseEntity<String> productShipped(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.statusShipped(id));
    }

    @Override
    @DeleteMapping("/delete/canceled")
    public ResponseEntity<Void> deleteAllCanceled() {
        deliveryService.deleteAllCanceled();
        return ResponseEntity.noContent().build();
    }
}
