package com.esand.clients.web.controller;

import com.esand.clients.service.ClientService;
import com.esand.clients.springdoc.SpringDoc;
import com.esand.clients.web.dto.ClientCreateDto;
import com.esand.clients.web.dto.ClientResponseDto;
import com.esand.clients.web.dto.ClientUpdateDto;
import com.esand.clients.web.dto.PageableDto;
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
@RequestMapping("api/v1/clients")
public class ClientController implements SpringDoc {
    private final ClientService clientService;

    @PostMapping
    public ResponseEntity<ClientResponseDto> create(@RequestBody @Valid ClientCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.save(dto));
    }

    @GetMapping
    public ResponseEntity<PageableDto> findAll(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(clientService.findAll(pageable));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<PageableDto> findByName(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable, @PathVariable String name) {
        return ResponseEntity.ok(clientService.findByName(pageable, name));
    }

    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ClientResponseDto> findByCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(clientService.findByCpf(cpf));
    }

    @GetMapping("/date")
    public ResponseEntity<PageableDto> findByDate(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                              @RequestParam(value = "afterDate", required = false) String afterDate,
                                              @RequestParam(value = "beforeDate", required = false) String beforeDate) {
        return ResponseEntity.ok(clientService.findClientsByDate(afterDate, beforeDate, pageable));
    }

    @PatchMapping("/edit/{cpf}")
    public ResponseEntity<Void> update(@PathVariable String cpf, @RequestBody @Valid ClientUpdateDto dto) {
        clientService.update(cpf, dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/delete/cpf/{cpf}")
    public ResponseEntity<Void> deleteClientByCpf(@PathVariable String cpf) {
        clientService.deleteByCpf(cpf);
        return ResponseEntity.noContent().build();
    }
}
