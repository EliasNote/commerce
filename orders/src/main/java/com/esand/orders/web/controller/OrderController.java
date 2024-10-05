package com.esand.orders.web.controller;

import com.esand.orders.service.OrderService;
import com.esand.orders.springdoc.SpringDoc;
import com.esand.orders.web.dto.OrderCreateDto;
import com.esand.orders.web.dto.OrderResponseDto;
import com.esand.orders.web.dto.PageableDto;
import io.swagger.v3.oas.annotations.Parameter;
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

    @PostMapping
    public ResponseEntity<OrderResponseDto> create(@RequestBody OrderCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.save(dto));
    }

    @GetMapping
    public ResponseEntity<PageableDto> findAll(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                               @RequestParam(value = "afterDate", required = false) String afterDate,
                                               @RequestParam(value = "beforeDate", required = false) String beforeDate) {
        return ResponseEntity.ok(orderService.findAll(afterDate, beforeDate, pageable));
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<PageableDto> findBySku(@PageableDefault(size = 10) Pageable pageable, @PathVariable String sku) {
        return ResponseEntity.ok(orderService.findBySku(pageable, sku));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<PageableDto> findByCpf(@PageableDefault(size = 10) Pageable pageable, @PathVariable String cpf) {
        return ResponseEntity.ok(orderService.findByCpf(pageable, cpf));
    }

    @PatchMapping("/processing/{id}")
    public ResponseEntity<String> sendOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.sendOrder(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/processing")
    public ResponseEntity<Void> deleteAllProcessing() {
        orderService.deleteAllProcessing();
        return ResponseEntity.noContent().build();
    }
}
