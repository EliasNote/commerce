package com.esand.clients.web.controller;

import com.esand.clients.entity.Client;
import com.esand.clients.service.ClientService;
import com.esand.clients.web.dto.ClientCreateDto;
import com.esand.clients.web.dto.ClientResponseDto;
import com.esand.clients.web.dto.ClientUpdateDto;
import com.esand.clients.web.dto.PageableDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;


class ClientControllerTest {
    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void create() {
        ClientCreateDto createDto =  new ClientCreateDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");
        ClientResponseDto responseDto = new ClientResponseDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");

        when(clientService.save(any(ClientCreateDto.class))).thenReturn(responseDto);

        ResponseEntity<ClientResponseDto> response = clientController.create(createDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test", response.getBody().getName());
        assertEquals("07021050070", response.getBody().getCpf());
        assertEquals("55210568972", response.getBody().getPhone());
        assertEquals("teste@email.com", response.getBody().getEmail());
        assertEquals("Address1", response.getBody().getAddress());
        assertEquals(LocalDate.of(2024, 8, 7), response.getBody().getBirthDate());
        assertEquals("M", response.getBody().getGender());
    }

    @Test
    void findAll() {
        ClientCreateDto createDto =  new ClientCreateDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");
        Pageable pageable = PageRequest.of(0, 10);
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(List.of(createDto));

        when(clientService.findAll(pageable)).thenReturn(pageableDto);

        ResponseEntity<PageableDto> response = clientController.findAll(pageable);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getContent());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void findByName() {
        ClientCreateDto createDto =  new ClientCreateDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");
        Pageable pageable = PageRequest.of(0, 10);
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(List.of(createDto));

        when(clientService.findByName(pageable, "Test")).thenReturn(pageableDto);

        ResponseEntity<PageableDto> response = clientController.findByName(pageable, "Test");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getContent());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void findByCpf() {
        ClientResponseDto responseDto =  new ClientResponseDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");

        when(clientService.findByCpf("07021050070")).thenReturn(responseDto);

        ResponseEntity<ClientResponseDto> response = clientController.findByCpf("07021050070");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test", response.getBody().getName());
        assertEquals("07021050070", response.getBody().getCpf());
        assertEquals("55210568972", response.getBody().getPhone());
        assertEquals("teste@email.com", response.getBody().getEmail());
        assertEquals("Address1", response.getBody().getAddress());
        assertEquals(LocalDate.of(2024, 8, 7), response.getBody().getBirthDate());
        assertEquals("M", response.getBody().getGender());
    }

    @Test
    void findByDate() {
        ClientCreateDto createDto =  new ClientCreateDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");
        Pageable pageable = PageRequest.of(0, 10);
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(List.of(createDto));

        when(clientService.findClientsByDate("2024-08-08", "2024-08-08", pageable)).thenReturn(pageableDto);

        ResponseEntity<PageableDto> response = clientController.findByDate(pageable, "2024-08-08", "2024-08-08");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getContent());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void update() {
        ClientUpdateDto updateDto =  new ClientUpdateDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");

        doNothing().when(clientService).update("07021050070", updateDto);

        ResponseEntity<Void> response = clientController.update("07021050070", updateDto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}