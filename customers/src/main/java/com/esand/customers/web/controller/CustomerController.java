package com.esand.customers.web.controller;

import com.esand.customers.service.CustomerService;
import com.esand.customers.springdoc.SpringDoc;
import com.esand.customers.web.dto.CustomerCreateDto;
import com.esand.customers.web.dto.CustomerResponseDto;
import com.esand.customers.web.dto.CustomerUpdateDto;
import com.esand.customers.web.dto.PageableDto;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/customers")
public class CustomerController implements SpringDoc {
    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<CustomerResponseDto> create(@RequestBody @Valid CustomerCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.save(dto));
    }

    @GetMapping
    public ResponseEntity<PageableDto> findAll(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                               @RequestParam(value = "afterDate", required = false) String afterDate,
                                               @RequestParam(value = "beforeDate", required = false) String beforeDate) {
        return ResponseEntity.ok(customerService.findAll(afterDate, beforeDate, pageable));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<PageableDto> findByName(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                                  @RequestParam(value = "afterDate", required = false) String afterDate,
                                                  @RequestParam(value = "beforeDate", required = false) String beforeDate,
                                                  @PathVariable String name) {
        return ResponseEntity.ok(customerService.findByName(name, afterDate, beforeDate, pageable));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<CustomerResponseDto> findByCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(customerService.findByCpf(cpf));
    }

    @PatchMapping("/edit/{cpf}")
    public ResponseEntity<Void> update(@PathVariable String cpf, @RequestBody @Valid CustomerUpdateDto dto) {
        customerService.update(cpf, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/{cpf}")
    public ResponseEntity<Void> deleteCustomerByCpf(@PathVariable String cpf) {
        customerService.deleteByCpf(cpf);
        return ResponseEntity.noContent().build();
    }
}
