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

class CustomerServiceTest {

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
    void testSaveCustomerSuccess() {
        CustomerCreateDto createDto = EntityMock.createDto();
        Customer customer = EntityMock.customer();
        CustomerResponseDto responseDto = EntityMock.customerResponseDto();

        when(customerMapper.toCustomer(any(CustomerCreateDto.class))).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerMapper.toDto(any(Customer.class))).thenReturn(responseDto);


        CustomerResponseDto response = customerService.save(createDto);

        assertNotNull(response);
        assertEquals(EntityMock.NAME, response.getName() );
        assertEquals(EntityMock.CPF, response.getCpf());
        assertEquals(EntityMock.PHONE, response.getPhone());
        assertEquals(EntityMock.EMAIL, response.getEmail());
        assertEquals(EntityMock.ADDRESS, response.getAddress());
        assertEquals(EntityMock.BIRTHDATE, response.getBirthDate());
        assertEquals(EntityMock.GENDER, response.getGender());
    }

    @Test
    void testSaveCustomerCpfUniqueViolationException() {
        CustomerCreateDto createDto = EntityMock.createDto();
        Customer customer = EntityMock.customer();

        when(customerMapper.toCustomer(any(CustomerCreateDto.class))).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(CpfUniqueViolationException.class, () -> customerService.save(createDto));
    }

    @Test
    void testFindAllCustomersSuccess() {
        Page<CustomerDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();


        when(customerRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = customerService.findAll(null,null,page.getPageable());

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindAllCustomersEntityNotFoundException() {
        Page<CustomerDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(customerRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> customerService.findAll(null, null, page.getPageable()));
    }

    @Test
    void testFindByNameSuccess() {
        Page<CustomerDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(customerRepository.findByNameIgnoreCaseContaining(any(String.class), any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = customerService.findByName(page.getPageable(), EntityMock.NAME);

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

        assertThrows(EntityNotFoundException.class, () -> customerService.findByName(page.getPageable(), EntityMock.NAME));
    }

    @Test
    void testFindByCpfSuccess() {
        Customer customer = EntityMock.customer();
        CustomerResponseDto responseDto = EntityMock.customerResponseDto();

        when(customerRepository.findByCpf(any(String.class))).thenReturn(Optional.of(customer));
        when(customerMapper.toDto(any(Customer.class))).thenReturn(responseDto);

        CustomerResponseDto response = customerService.findByCpf(EntityMock.CPF);

        assertNotNull(response);
        assertEquals(EntityMock.NAME, response.getName() );
        assertEquals(EntityMock.CPF, response.getCpf());
        assertEquals(EntityMock.PHONE, response.getPhone());
        assertEquals(EntityMock.EMAIL, response.getEmail());
        assertEquals(EntityMock.ADDRESS, response.getAddress());
        assertEquals(EntityMock.BIRTHDATE, response.getBirthDate());
        assertEquals(EntityMock.GENDER, response.getGender());
    }

    @Test
    void testFindByCpfEntityNotFoundException() {
        when(customerRepository.findByCpf(any(String.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.findByCpf(EntityMock.CPF));
    }

//    @Test
//    void findCustomersByDateBetweenSuccess() {
//        Page<CustomerDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(customerRepository.findByCreateDateBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
//        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = customerService.findCustomersByDate(LocalDate.now().minusDays(1).toString(), LocalDate.now().plusDays(1).toString(), page.getPageable());
//
//        assertNotNull(response);
//        assertNotNull(response.getContent());
//        assertNotNull(response.getContent().get(0));
//        assertEquals(1, response.getContent().size());
//    }
//
//    @Test
//    void findCustomersByDateAfterSuccess() {
//        Page<CustomerDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(customerRepository.findByCreateDateAfter(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
//        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = customerService.findCustomersByDate(LocalDate.now().minusDays(1).toString(), null, page.getPageable());
//
//        assertNotNull(response);
//        assertNotNull(response.getContent());
//        assertNotNull(response.getContent().get(0));
//        assertEquals(1, response.getContent().size());
//    }
//
//    @Test
//    void findCustomersByDateBeforeSuccess() {
//        Page<CustomerDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(customerRepository.findByCreateDateBefore(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
//        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = customerService.findCustomersByDate(null, LocalDate.now().plusDays(1).toString(), page.getPageable());
//
//        assertNotNull(response);
//        assertNotNull(response.getContent());
//        assertNotNull(response.getContent().get(0));
//        assertEquals(1, response.getContent().size());
//    }
//
//    @Test
//    void testFindCustomersByDateNoDateParametersProvided() {
//        assertThrows(EntityNotFoundException.class, () -> customerService.findCustomersByDate(null, null, EntityMock.page().getPageable()));
//    }
//
//    @Test
//    void findCustomersByDateEntityNotFoundException() {
//        Page<CustomerDtoPagination> page = EntityMock.pageEmpty();
//        PageableDto pageableDto = EntityMock.pageableDtoEmpty();
//
//        when(customerRepository.findByCreateDateBefore(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
//        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        assertThrows(EntityNotFoundException.class, () -> customerService.findCustomersByDate(null, LocalDate.now().plusDays(1).toString(), page.getPageable()));
//    }

    @Test
    void testUpdateSuccess() {
        Customer customer = EntityMock.customer();
        CustomerUpdateDto updateDto =  EntityMock.customerUpdateDto();

        when(customerRepository.findByCpf(any(String.class))).thenReturn(Optional.of(customer));
        doNothing().when(customerMapper).updateCustomer(any(CustomerUpdateDto.class), any(Customer.class));

        customerService.update(EntityMock.CPF, updateDto);
    }

    @Test
    void testUpdateEntityNotFoundException() {
        CustomerUpdateDto updateDto =  EntityMock.customerUpdateDto();

        when(customerRepository.findByCpf(any(String.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.update(EntityMock.CPF, updateDto));
    }

    @Test
    void testDeleteProductByCpfSuccess() {
        when(customerRepository.existsByCpf(any(String.class))).thenReturn(true);

        customerService.deleteByCpf(EntityMock.CPF);
    }

    @Test
    void testDeleteProductByCpfEntityNotFoundException() {
        when(customerRepository.existsByCpf(any(String.class))).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> customerService.deleteByCpf(EntityMock.CPF));
    }
}