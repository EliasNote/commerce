package com.esand.customers.service;

import com.esand.customers.entity.Customer;
import com.esand.customers.entity.EntityMock;
import com.esand.customers.exception.CpfUniqueViolationException;
import com.esand.customers.exception.EntityNotFoundException;
import com.esand.customers.repository.CustomerRepository;
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
import org.mockito.Mock;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    void verifyResult(Object object, CustomerResponseDto expect) {
        CustomerResponseDto response;

        if (object instanceof PageableDto) {
            PageableDto page = (PageableDto) object;
            assertNotNull(page);
            assertNotNull(page.getContent());
            assertEquals(1, page.getContent().size());

            response = (CustomerResponseDto) page.getContent().getFirst();
        } else {
            response = (CustomerResponseDto) object;
        }

        assertNotNull(response);
        assertEquals(expect.getName(), response.getName() );
        assertEquals(expect.getCpf(), response.getCpf());
        assertEquals(expect.getPhone(), response.getPhone());
        assertEquals(expect.getEmail(), response.getEmail());
        assertEquals(expect.getAddress(), response.getAddress());
        assertEquals(expect.getBirthDate(), response.getBirthDate());
        assertEquals(expect.getGender(), response.getGender());
        assertNotNull(response.getCreateDate());
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

        verifyResult(response, EntityMock.customerResponseDto());
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
        Page<CustomerResponseDto> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();


        when(customerRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = customerService.findAll(null,null,page.getPageable());

        verifyResult(response, EntityMock.customerResponseDto());
    }

    @Test
    void testFindAllCustomersAndDateBetweenSuccess() {
        Page<CustomerResponseDto> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String after = LocalDate.now().minusDays(1).toString();
        String before = LocalDate.now().plusDays(1).toString();

        when(customerRepository.findByCreateDateBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = customerService.findAll(after, before,page.getPageable());

        verifyResult(response, EntityMock.customerResponseDto());
    }

    @Test
    void testFindAllCustomersAndDateAfterSuccess() {
        Page<CustomerResponseDto> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String after = LocalDate.now().minusDays(1).toString();

        when(customerRepository.findByCreateDateAfter(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = customerService.findAll(after, null,page.getPageable());

        verifyResult(response, EntityMock.customerResponseDto());
    }

    @Test
    void testFindAllCustomersAndDateBeforeSuccess() {
        Page<CustomerResponseDto> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String before = LocalDate.now().plusDays(1).toString();

        when(customerRepository.findByCreateDateBefore(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = customerService.findAll(null, before,page.getPageable());

        verifyResult(response, EntityMock.customerResponseDto());
    }

    @Test
    void testFindAllCustomersEntityNotFoundException() {
        Page<CustomerResponseDto> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(customerRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> customerService.findAll(null, null, page.getPageable()));
    }

    @Test
    void testFindByNameSuccess() {
        Page<CustomerResponseDto> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(customerRepository.findByNameIgnoreCaseContaining(any(String.class), any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = customerService.findByName(EntityMock.NAME, null, null, page.getPageable());

        verifyResult(response, EntityMock.customerResponseDto());
    }

    @Test
    void findCustomersByNameAndDateBetweenSuccess() {
        Page<CustomerResponseDto> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String after = LocalDate.now().minusDays(1).toString();
        String before = LocalDate.now().plusDays(1).toString();

        when(customerRepository.findByNameIgnoreCaseContainingAndCreateDateBetween(anyString(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = customerService.findByName(EntityMock.NAME, after, before, page.getPageable());

        verifyResult(response, EntityMock.customerResponseDto());
    }

    @Test
    void findCustomersByNameAndDateAfterSuccess() {
        Page<CustomerResponseDto> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String after = LocalDate.now().minusDays(1).toString();

        when(customerRepository.findByNameIgnoreCaseContainingAndCreateDateAfter(anyString(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = customerService.findByName(EntityMock.NAME, after, null, page.getPageable());

        verifyResult(response, EntityMock.customerResponseDto());
    }

    @Test
    void findCustomersByNameAndDateBeforeSuccess() {
        Page<CustomerResponseDto> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String before = LocalDate.now().plusDays(1).toString();

        when(customerRepository.findByNameIgnoreCaseContainingAndCreateDateBefore(anyString(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = customerService.findByName(EntityMock.NAME, null, before, page.getPageable());

        verifyResult(response, EntityMock.customerResponseDto());
    }

    @Test
    void testFindByNameEntityNotFoundException() {
        Page<CustomerResponseDto> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(customerRepository.findByNameIgnoreCaseContaining(any(String.class), any(Pageable.class))).thenReturn(page);
        when(customerMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> customerService.findByName(EntityMock.NAME, null, null, page.getPageable()));
    }

    @Test
    void testFindByCpfSuccess() {
        Customer customer = EntityMock.customer();
        CustomerResponseDto responseDto = EntityMock.customerResponseDto();

        when(customerRepository.findByCpf(any(String.class))).thenReturn(Optional.of(customer));
        when(customerMapper.toDto(any(Customer.class))).thenReturn(responseDto);

        CustomerResponseDto response = customerService.findByCpf(EntityMock.CPF);

        verifyResult(response, EntityMock.customerResponseDto());
    }

    @Test
    void testFindByCpfEntityNotFoundException() {
        when(customerRepository.findByCpf(any(String.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> customerService.findByCpf(EntityMock.CPF));
    }

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