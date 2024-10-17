package com.esand.customers.service;

import com.esand.customers.entity.Customer;
import com.esand.customers.exception.CpfUniqueViolationException;
import com.esand.customers.exception.EntityNotFoundException;
import com.esand.customers.repository.CustomerRepository;
import com.esand.customers.web.dto.CustomerCreateDto;
import com.esand.customers.web.dto.CustomerResponseDto;
import com.esand.customers.web.dto.CustomerUpdateDto;
import com.esand.customers.web.dto.PageableDto;
import com.esand.customers.web.mapper.CustomerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RequiredArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    public CustomerResponseDto save(CustomerCreateDto dto) {
        try {
            Customer customer = customerRepository.save(customerMapper.toCustomer(dto));
            return customerMapper.toDto(customer);
        } catch (DataIntegrityViolationException ex) {
            throw new CpfUniqueViolationException(String.format("CPF %s cannot be registered, there is already a registered customer with an informed CPF", dto.getCpf()));
        }
    }

    @Transactional
    public PageableDto findAll(String afterDate, String beforeDate, Pageable pageable) {
        return findByCriteria(null, afterDate, beforeDate, pageable);
    }

    @Transactional
    public PageableDto findByName(String afterDate, String beforeDate, String name, Pageable pageable) {
        return findByCriteria(name, afterDate, beforeDate, pageable);
    }

    @Transactional(readOnly = true)
    public CustomerResponseDto findByCpf(String cpf) {
        return customerMapper.toDto(findCustomerByCpf(cpf));
    }

    @Transactional
    public void update(String cpf, CustomerUpdateDto dto) {
        Customer customer = findCustomerByCpf(cpf);
        customerMapper.updateCustomer(dto, customer);
    }

    @Transactional
    public void deleteByCpf(String cpf) {
        if (!customerRepository.existsByCpf(cpf)) {
            throw new EntityNotFoundException("Customer not found by CPF");
        }
        customerRepository.deleteByCpf(cpf);
    }

    private Customer findCustomerByCpf(String cpf) {
        return customerRepository.findByCpf(cpf).orElseThrow(
                () -> new EntityNotFoundException("Customer not found by CPF")
        );
    }

    @Transactional
    private PageableDto findByCriteria(String name, String afterDate, String beforeDate, Pageable pageable) {
        LocalDateTime after = null;
        LocalDateTime before = null;
        PageableDto dto;

        if (afterDate != null) {
            after = LocalDate.parse(afterDate).atStartOfDay();
        }
        if (beforeDate != null) {
            before = LocalDate.parse(beforeDate).atTime(LocalTime.MAX);
        }

        if (name != null) {
            if (after != null && before != null) {
                dto = customerMapper.toPageableDto(customerRepository.findByNameIgnoreCaseContainingAndCreateDateBetween(name, after, before, pageable));
            } else if (after != null) {
                dto = customerMapper.toPageableDto(customerRepository.findByNameIgnoreCaseContainingAndCreateDateAfter(name, after, pageable));
            } else if (before != null) {
                dto = customerMapper.toPageableDto(customerRepository.findByNameIgnoreCaseContainingAndCreateDateBefore(name, before, pageable));
            } else {
                dto = customerMapper.toPageableDto(customerRepository.findByNameIgnoreCaseContaining(name, pageable));
            }
        } else {
            if (after != null && before != null) {
                dto = customerMapper.toPageableDto(customerRepository.findByCreateDateBetween(after, before, pageable));
            } else if (after != null) {
                dto = customerMapper.toPageableDto(customerRepository.findByCreateDateAfter(after, pageable));
            } else if (before != null) {
                dto = customerMapper.toPageableDto(customerRepository.findByCreateDateBefore(before, pageable));
            } else {
                dto = customerMapper.toPageableDto(customerRepository.findAllPageable(pageable));
            }
        }

        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No customers found");
        }

        return dto;
    }
}
