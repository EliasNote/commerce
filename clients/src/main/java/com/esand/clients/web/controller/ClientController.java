package com.esand.clients.web.controller;

import com.esand.clients.service.ClientService;
import com.esand.clients.web.dto.ClientCreateDto;
import com.esand.clients.web.dto.ClientResponseDto;
import com.esand.clients.web.dto.PageableDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/clients")
public class ClientController {
    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponseDto> create(@RequestBody ClientCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.save(dto));
    }

    @GetMapping
    public ResponseEntity<PageableDto> findAll(@PageableDefault(size = 5) Pageable pageable) {
        return ResponseEntity.ok(clientService.findAll(pageable));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<ClientResponseDto> findByName(@PathVariable String name) {
        return ResponseEntity.ok(clientService.findByName(name));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClientResponseDto> findByCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(clientService.findByCpf(cpf));
    }

    @PatchMapping("/edit/{cpf}")
    public ResponseEntity<Void> update(@PathVariable String cpf, @RequestBody ClientCreateDto dto) {
        clientService.update(cpf, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/status/{cpf}")
    public ResponseEntity<String> toggleStatus(@PathVariable String cpf) {
        return ResponseEntity.ok("updated status for: " + clientService.alter(cpf));
    }
}
