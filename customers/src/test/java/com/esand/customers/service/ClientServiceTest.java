package com.esand.customers.service;

import com.esand.customers.entity.Customer;
import com.esand.customers.entity.EntityMock;
import com.esand.customers.exception.CpfUniqueViolationException;
import com.esand.customers.exception.EntityNotFoundException;
import com.esand.customers.repository.CustomerRepository;
import com.esand.customers.repository.pagination.CustomerDtoPagination;
import com.esand.customers.web.dto.CustomerCreateDto;
import com.esand.customers.web.dto.CustomerResponseDto;
import com.esand.customers.web.dto.CustomerUpdateDto;
import com.esand.customers.web.dto.PageableDto;
import com.esand.customers.web.mapper.CustomerMapper;
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
    private CustomerRepository customerRepository;

    @org.mockito.Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveClientSuccess() {
        CustomerCreateDto createDto = EntityMock.createDto();
        Customer customer = EntityMock.client();
        CustomerResponseDto responseDto = EntityMock.clientResponseDto();

        when(customerMapper.toClient(any(CustomerCreateDto.class))).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerMapper.toDto(any(Customer.class))).thenReturn(responseDto);


        CustomerResponseDto response = customerService.save(createDto);

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
        CustomerCreateDto createDto = EntityMock.createDto();
        Customer customer = EntityMock.client();

        when(customerMapper.toClient(any(CustomerCreateDto.class))).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(CpfUniqueViolationException.class, () -> customerService.save(createDto));
    }

    @Test
    void testFindAllClientsSuccess() {
        Page<CustomerDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(customerRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = customerService.findAll(page.getPageable());

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindAllClientsEntityNotFoundException() {
        Page<CustomerDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(customerRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> customerService.findAll(page.getPageable()));
    }

    @Test
    void testFindByNameSuccess() {
        Page<CustomerDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(customerRepository.findByNameIgnoreCaseContaining(any(String.class), any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = customerService.findByName(page.getPageable(), "Test");

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindByNameEntityNotFoundException() {
        Page<CustomerDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(customerRepository.findByNameIgnoreCaseContaining(any(String.class), any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> customerService.findByName(page.getPageable(), "Test"));
    }

    @Test
    void testFindByCpfSuccess() {
        Customer customer = EntityMock.client();
        CustomerResponseDto responseDto = EntityMock.clientResponseDto();

        when(customerRepository.findByCpf(any(String.class))).thenReturn(Optional.of(customer));
        when(customerMapper.toDto(any(Customer.class))).thenReturn(responseDto);

        CustomerResponseDto response = customerService.findByCpf("07021050070");

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
        when(customerRepository.findByCpf(any(String.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.findByCpf("07021050070"));
    }

    @Test
    void findClientsByDateBetweenSuccess() {
        Page<CustomerDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(customerRepository.findByCreateDateBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = customerService.findClientsByDate(LocalDate.now().minusDays(1).toString(), LocalDate.now().plusDays(1).toString(), page.getPageable());

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void findClientsByDateAfterSuccess() {
        Page<CustomerDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(customerRepository.findByCreateDateAfter(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = customerService.findClientsByDate(LocalDate.now().minusDays(1).toString(), null, page.getPageable());

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void findClientsByDateBeforeSuccess() {
        Page<CustomerDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(customerRepository.findByCreateDateBefore(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = customerService.findClientsByDate(null, LocalDate.now().plusDays(1).toString(), page.getPageable());

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindClientsByDateNoDateParametersProvided() {
        assertThrows(EntityNotFoundException.class, () -> customerService.findClientsByDate(null, null, EntityMock.page().getPageable()));
    }

    @Test
    void findClientsByDateEntityNotFoundException() {
        Page<CustomerDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(customerRepository.findByCreateDateBefore(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> customerService.findClientsByDate(null, LocalDate.now().plusDays(1).toString(), page.getPageable()));
    }

    @Test
    void testUpdateSuccess() {
        Customer customer = EntityMock.client();
        CustomerUpdateDto updateDto =  EntityMock.clientUpdateDto();

        when(customerRepository.findByCpf(any(String.class))).thenReturn(Optional.of(customer));
        doNothing().when(customerMapper).updateClient(any(CustomerUpdateDto.class), any(Customer.class));

        customerService.update("07021050070", updateDto);
    }

    @Test
    void testUpdateEntityNotFoundException() {
        CustomerUpdateDto updateDto =  EntityMock.clientUpdateDto();

        when(customerRepository.findByCpf(any(String.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.update("07021050070", updateDto));
    }

    @Test
    void testDeleteProductByCpfSuccess() {
        when(customerRepository.existsByCpf(any(String.class))).thenReturn(true);

        customerService.deleteByCpf("07021050070");
    }

    @Test
    void testDeleteProductByCpfEntityNotFoundException() {
        when(customerRepository.existsByCpf(any(String.class))).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> customerService.deleteByCpf("07021050070"));
    }
}