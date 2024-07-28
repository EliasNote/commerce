package com.esand.orders.web.controller;

import com.esand.orders.service.OrderService;
import com.esand.orders.springdoc.SpringDoc;
import com.esand.orders.web.dto.OrderCreateDto;
import com.esand.orders.web.dto.OrderResponseDto;
import com.esand.orders.web.dto.PageableDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController implements SpringDoc {
    private final OrderService orderService;

    @Override
    @PostMapping
    public ResponseEntity<OrderResponseDto> create(@RequestBody OrderCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.save(dto));
    }

    @Override
    @GetMapping
    public ResponseEntity<PageableDto> findAll(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(orderService.findAll(pageable));
    }

    @Override
    @GetMapping("/sku/{sku}")
    public ResponseEntity<PageableDto> findBySku(@PageableDefault(size = 10) Pageable pageable, @PathVariable String sku) {
        return ResponseEntity.ok(orderService.findBySku(pageable, sku));
    }

    @Override
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PageableDto> findByCpf(@PageableDefault(size = 10) Pageable pageable, @PathVariable String cpf) {
        return ResponseEntity.ok(orderService.findByCpf(pageable, cpf));
    }

    @Override
    @PatchMapping("/processed/{id}")
    public ResponseEntity<String> processOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.sendOrder(id));
    }

    @Override
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/delete/processed")
    public ResponseEntity<Void> deleteAllProcessed() {
        orderService.deleteAllProcessed();
        return ResponseEntity.noContent().build();
    }
}
