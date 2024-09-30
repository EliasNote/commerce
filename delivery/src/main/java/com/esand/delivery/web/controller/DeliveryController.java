package com.esand.delivery.web.controller;

import com.esand.delivery.service.DeliveryService;
import com.esand.delivery.springdoc.SpringDoc;
import com.esand.delivery.web.dto.DeliveryResponseDto;
import com.esand.delivery.web.dto.PageableDto;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/deliveries")
public class DeliveryController implements SpringDoc {

    private final DeliveryService deliveryService;

    @GetMapping
    public ResponseEntity<PageableDto> findAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(deliveryService.findAll(pageable));
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<DeliveryResponseDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.findById(id));
    }

    @GetMapping("/shipped")
    public ResponseEntity<PageableDto> findAllShipped(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(deliveryService.findAllShipped(pageable));
    }

    @GetMapping("/processing")
    public ResponseEntity<PageableDto> findAllProcessing(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(deliveryService.findAllProcessing(pageable));
    }

    @GetMapping("/canceled")
    public ResponseEntity<PageableDto> findAllCanceled(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(deliveryService.findAllCanceled(pageable));
    }

    @GetMapping("/date")
    public ResponseEntity<PageableDto> findByDate(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                                  @RequestParam(value = "afterDate", required = false) String afterDate,
                                                  @RequestParam(value = "beforeDate", required = false) String beforeDate) {
        return ResponseEntity.ok(deliveryService.findDeliveryByDate(afterDate, beforeDate, pageable));
    @GetMapping("/top-shipped-customers")
    public ResponseEntity<String> findTopShippedCustomers(@RequestParam(value = "afterDate", required = false) String afterDate,
                                                          @RequestParam(value = "beforeDate", required = false) String beforeDate) {
        return ResponseEntity.ok(deliveryService.findTopShippedByCustomers(afterDate, beforeDate));
    }

    @GetMapping("/top-shipped-products")
    public ResponseEntity<String> findTopShippedProducts(@RequestParam(value = "afterDate", required = false) String afterDate,
                                                         @RequestParam(value = "beforeDate", required = false) String beforeDate) {
        return ResponseEntity.ok(deliveryService.findTopShippedByProducts(afterDate, beforeDate));
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<String> cancelDelivery(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.cancel(id));
    }

    @PatchMapping("/shipped/{id}")
    public ResponseEntity<String> productShipped(@PathVariable Long id) {
        return ResponseEntity.ok(deliveryService.statusShipped(id));
    }

    @DeleteMapping("/delete/canceled")
    public ResponseEntity<Void> deleteAllCanceled() {
        deliveryService.deleteAllCanceled();
        return ResponseEntity.noContent().build();
    }
}
