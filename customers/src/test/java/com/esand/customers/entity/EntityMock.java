package com.esand.customers.entity;

import com.esand.customers.web.dto.CustomerCreateDto;
import com.esand.customers.web.dto.CustomerResponseDto;
import com.esand.customers.web.dto.CustomerUpdateDto;
import com.esand.customers.web.dto.PageableDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class EntityMock {

    public static final String NAME = "Teste";
    public static final String CPF = "07021050070";
    public static final String PHONE = "55210568972";
    public static final String EMAIL = "teste@email.com";
    public static final String ADDRESS = "Address111";
    public static final LocalDate BIRTHDATE = LocalDate.of(2024, 8, 7);
    public static final Customer.Gender GENDER = Customer.Gender.M;

    public static CustomerCreateDto createDto() {
        return new CustomerCreateDto(
                NAME,
                CPF,
                PHONE,
                EMAIL,
                ADDRESS,
                BIRTHDATE,
                GENDER.toString()
        );
    }

    public static Customer customer() {
        return new Customer(
                1L,
                NAME,
                CPF,
                PHONE,
                EMAIL,
                ADDRESS,
                BIRTHDATE,
                Customer.Gender.M,
                LocalDateTime.now()
        );
    }

    public static CustomerResponseDto customerResponseDto() {
        return new CustomerResponseDto(
                NAME,
                CPF,
                PHONE,
                EMAIL,
                ADDRESS,
                BIRTHDATE,
                GENDER,
                LocalDateTime.now()
        );
    }

    public static Page<CustomerResponseDto> page() {
        Pageable pageable = PageRequest.of(0, 10);
        List<CustomerResponseDto> content = List.of(
                customerResponseDto()
        );
        return new PageImpl<>(content, pageable, content.size());
    }

    public static PageableDto pageableDto() {
        List<CustomerResponseDto> content = List.of(
                customerResponseDto()
        );
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);
        return pageableDto;
    }


    public static Page<CustomerResponseDto> pageEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        List<CustomerResponseDto> content = List.of();
        return new PageImpl<>(content, pageable, content.size());
    }

    public static PageableDto pageableDtoEmpty() {
        List<CustomerResponseDto> content = List.of();
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);
        return pageableDto;
    }

    public static CustomerUpdateDto customerUpdateDto() {
        return new CustomerUpdateDto(
                "Asdadsasd",
                CPF,
                PHONE,
                EMAIL,
                ADDRESS,
                BIRTHDATE,
                GENDER.toString()
        );
    }
}
