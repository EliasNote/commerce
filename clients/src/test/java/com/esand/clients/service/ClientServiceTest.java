package com.esand.clients.service;

import com.esand.clients.entity.Client;
import com.esand.clients.entity.EntityMock;
import com.esand.clients.exception.CpfUniqueViolationException;
import com.esand.clients.exception.EntityNotFoundException;
import com.esand.clients.repository.ClientRepository;
import com.esand.clients.repository.pagination.ClientDtoPagination;
import com.esand.clients.web.dto.ClientCreateDto;
import com.esand.clients.web.dto.ClientResponseDto;
import com.esand.clients.web.dto.ClientUpdateDto;
import com.esand.clients.web.dto.PageableDto;
import com.esand.clients.web.mapper.ClientMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class ClientServiceTest {

    @org.mockito.Mock
    private ClientRepository clientRepository;

    @org.mockito.Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveClientSuccess() {
        ClientCreateDto createDto = EntityMock.createDto();
        Client client = EntityMock.client();
        ClientResponseDto responseDto = EntityMock.clientResponseDto();

        when(clientMapper.toClient(any(ClientCreateDto.class))).thenReturn(client);
        when(clientRepository.save(any(Client.class))).thenReturn(client);
        when(clientMapper.toDto(any(Client.class))).thenReturn(responseDto);


        ClientResponseDto response = clientService.save(createDto);

        assertNotNull(response);
        assertEquals("Test", response.getName() );
        assertEquals("07021050070", response.getCpf());
        assertEquals("55210568972", response.getPhone());
        assertEquals("teste@email.com", response.getEmail());
        assertEquals("Address1", response.getAddress());
        assertEquals(LocalDate.of(2024, 8, 7), response.getBirthDate());
        assertEquals("M", response.getGender());
    }

    @Test
    void testSaveClientCpfUniqueViolationException() {
        ClientCreateDto createDto = EntityMock.createDto();
        Client client = EntityMock.client();

        when(clientMapper.toClient(any(ClientCreateDto.class))).thenReturn(client);
        when(clientRepository.save(any(Client.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(CpfUniqueViolationException.class, () -> clientService.save(createDto));
    }

    @Test
    void testFindAllClientsSuccess() {
        Page<ClientDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(clientRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
        when(clientMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = clientService.findAll(page.getPageable());

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindAllClientsEntityNotFoundException() {
        Page<ClientDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(clientRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
        when(clientMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> clientService.findAll(page.getPageable()));
    }

    @Test
    void testFindByNameSuccess() {
        Page<ClientDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(clientRepository.findByNameIgnoreCaseContaining(any(String.class), any(Pageable.class))).thenReturn(page);
        when(clientMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = clientService.findByName(page.getPageable(), "Test");

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindByNameEntityNotFoundException() {
        Page<ClientDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(clientRepository.findByNameIgnoreCaseContaining(any(String.class), any(Pageable.class))).thenReturn(page);
        when(clientMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> clientService.findByName(page.getPageable(), "Test"));
    }

    @Test
    void testFindByCpfSuccess() {
        Client client = EntityMock.client();
        ClientResponseDto responseDto = EntityMock.clientResponseDto();

        when(clientRepository.findByCpf(any(String.class))).thenReturn(Optional.of(client));
        when(clientMapper.toDto(any(Client.class))).thenReturn(responseDto);

        ClientResponseDto response = clientService.findByCpf("07021050070");

        assertNotNull(response);
        assertEquals("Test", response.getName() );
        assertEquals("07021050070", response.getCpf());
        assertEquals("55210568972", response.getPhone());
        assertEquals("teste@email.com", response.getEmail());
        assertEquals("Address1", response.getAddress());
        assertEquals(LocalDate.of(2024, 8, 7), response.getBirthDate());
        assertEquals("M", response.getGender());
    }

    @Test
    void testFindByCpfEntityNotFoundException() {
        when(clientRepository.findByCpf(any(String.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clientService.findByCpf("07021050070"));
    }

    @Test
    void findClientsByDateBetweenSuccess() {
        Page<ClientDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(clientRepository.findByCreateDateBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(clientMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = clientService.findClientsByDate(LocalDate.now().minusDays(1).toString(), LocalDate.now().plusDays(1).toString(), page.getPageable());

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void findClientsByDateAfterSuccess() {
        Page<ClientDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(clientRepository.findByCreateDateAfter(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(clientMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = clientService.findClientsByDate(LocalDate.now().minusDays(1).toString(), null, page.getPageable());

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void findClientsByDateBeforeSuccess() {
        Page<ClientDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(clientRepository.findByCreateDateBefore(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(clientMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = clientService.findClientsByDate(null, LocalDate.now().plusDays(1).toString(), page.getPageable());

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindClientsByDateNoDateParametersProvided() {
        assertThrows(EntityNotFoundException.class, () -> clientService.findClientsByDate(null, null, EntityMock.page().getPageable()));
    }

    @Test
    void findClientsByDateEntityNotFoundException() {
        Page<ClientDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(clientRepository.findByCreateDateBefore(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(clientMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> clientService.findClientsByDate(null, LocalDate.now().plusDays(1).toString(), page.getPageable()));
    }

    @Test
    void testUpdateSuccess() {
        Client client = EntityMock.client();
        ClientUpdateDto updateDto =  EntityMock.clientUpdateDto();

        when(clientRepository.findByCpf(any(String.class))).thenReturn(Optional.of(client));
        doNothing().when(clientMapper).updateClient(any(ClientUpdateDto.class), any(Client.class));

        clientService.update("07021050070", updateDto);
    }

    @Test
    void testUpdateEntityNotFoundException() {
        ClientUpdateDto updateDto =  EntityMock.clientUpdateDto();

        when(clientRepository.findByCpf(any(String.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> clientService.update("07021050070", updateDto));
    }

    @Test
    void testDeleteProductByCpfSuccess() {
        when(clientRepository.existsByCpf(any(String.class))).thenReturn(true);

        clientService.deleteByCpf("07021050070");
    }

    @Test
    void testDeleteProductByCpfEntityNotFoundException() {
        when(clientRepository.existsByCpf(any(String.class))).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> clientService.deleteByCpf("07021050070"));
    }
}