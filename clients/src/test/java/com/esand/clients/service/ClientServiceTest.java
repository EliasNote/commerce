package com.esand.clients.service;

import com.esand.clients.entity.Client;
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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientService clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveClientSuccess() {
        ClientCreateDto createDto =  new ClientCreateDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");
        Client client = new Client(1L, "Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), Client.Gender.M, LocalDateTime.now());
        ClientResponseDto responseDto = new ClientResponseDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");

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
        ClientCreateDto createDto =  new ClientCreateDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");
        Client client = new Client(1L, "Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), Client.Gender.M, LocalDateTime.now());

        when(clientMapper.toClient(any(ClientCreateDto.class))).thenReturn(client);
        when(clientRepository.save(any(Client.class))).thenThrow(CpfUniqueViolationException.class);

        assertThrows(CpfUniqueViolationException.class, () -> clientService.save(createDto));
    }

    @Test
    void testFindAllClientsSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ClientDtoPagination> content = List.of(
                new ClientDtoPagination() {
                    public String getName() { return "Test"; }
                    public String getCpf() { return "07021050070"; }
                    public String getPhone() { return "55210568972"; }
                    public String getEmail() { return "teste@email.com"; }
                    public String getAddress() { return "Address1"; }
                    public LocalDate getBirthDate() { return LocalDate.of(2024, 8, 7); }
                    public String getGender() { return "M"; }
                }
        );

        Page<ClientDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(clientRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
        when(clientMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = clientService.findAll(pageable);

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindAllClientsEntityNotFoundException() {
        Pageable pageable = PageRequest.of(0, 10);

        when(clientRepository.findAllPageable(any(Pageable.class))).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> clientService.findAll(pageable));
    }

    @Test
    void testFindByNameSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ClientDtoPagination> content = List.of(
                new ClientDtoPagination() {
                    public String getName() { return "Test"; }
                    public String getCpf() { return "07021050070"; }
                    public String getPhone() { return "55210568972"; }
                    public String getEmail() { return "teste@email.com"; }
                    public String getAddress() { return "Address1"; }
                    public LocalDate getBirthDate() { return LocalDate.of(2024, 8, 7); }
                    public String getGender() { return "M"; }
                }
        );

        Page<ClientDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(clientRepository.findByNameIgnoreCase(any(String.class), any(Pageable.class))).thenReturn(page);
        when(clientMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = clientService.findByName(pageable, "Test");

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindByNameEntityNotFoundException() {
        Pageable pageable = PageRequest.of(0, 10);

        when(clientRepository.findByNameIgnoreCase(any(String.class), any(Pageable.class))).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> clientService.findByName(pageable, "Test"));
    }

    @Test
    void testFindByCpfSuccess() {
        Client client = new Client(1L, "Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), Client.Gender.M, LocalDateTime.now());
        ClientResponseDto responseDto = new ClientResponseDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");

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
        when(clientRepository.findByCpf(any(String.class))).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> clientService.findByCpf("07021050070"));
    }

    @Test
    void findClientsByDateBetweenSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ClientDtoPagination> content = List.of(
                new ClientDtoPagination() {
                    public String getName() { return "Test"; }
                    public String getCpf() { return "07021050070"; }
                    public String getPhone() { return "55210568972"; }
                    public String getEmail() { return "teste@email.com"; }
                    public String getAddress() { return "Address1"; }
                    public LocalDate getBirthDate() { return LocalDate.of(2024, 8, 7); }
                    public String getGender() { return "M"; }
                }
        );

        Page<ClientDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(clientRepository.findByCreateDateBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(clientMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = clientService.findClientsByDate(LocalDate.now().minusDays(1).toString(), LocalDate.now().plusDays(1).toString(), pageable);

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void findClientsByDateAfterSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ClientDtoPagination> content = List.of(
                new ClientDtoPagination() {
                    public String getName() { return "Test"; }
                    public String getCpf() { return "07021050070"; }
                    public String getPhone() { return "55210568972"; }
                    public String getEmail() { return "teste@email.com"; }
                    public String getAddress() { return "Address1"; }
                    public LocalDate getBirthDate() { return LocalDate.of(2024, 8, 7); }
                    public String getGender() { return "M"; }
                }
        );

        Page<ClientDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(clientRepository.findByCreateDateAfter(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(clientMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = clientService.findClientsByDate(LocalDate.now().minusDays(1).toString(), null, pageable);

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void findClientsByDateBeforeSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ClientDtoPagination> content = List.of(
                new ClientDtoPagination() {
                    public String getName() { return "Test"; }
                    public String getCpf() { return "07021050070"; }
                    public String getPhone() { return "55210568972"; }
                    public String getEmail() { return "teste@email.com"; }
                    public String getAddress() { return "Address1"; }
                    public LocalDate getBirthDate() { return LocalDate.of(2024, 8, 7); }
                    public String getGender() { return "M"; }
                }
        );

        Page<ClientDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(clientRepository.findByCreateDateBefore(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(clientMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = clientService.findClientsByDate(null, LocalDate.now().plusDays(1).toString(), pageable);

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void findClientsByDateEntityNotFoundException() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(EntityNotFoundException.class, () -> clientService.findClientsByDate(null, null, pageable));
    }

    @Test
    void testUpdateSuccess() {
        Client client = new Client(1L, "Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), Client.Gender.M, LocalDateTime.now());
        ClientUpdateDto updateDto =  new ClientUpdateDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");

        when(clientRepository.findByCpf(any(String.class))).thenReturn(Optional.of(client));
        doNothing().when(clientMapper).updateClient(any(ClientUpdateDto.class), any(Client.class));

        clientService.update("07021050070", updateDto);
    }

    @Test
    void testUpdateEntityNotFoundException() {
        ClientUpdateDto updateDto =  new ClientUpdateDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");

        when(clientRepository.findByCpf(any(String.class))).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> clientService.update("07021050070", updateDto));
    }
}