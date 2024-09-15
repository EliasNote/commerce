package com.esand.customers.entity;

import com.esand.customers.repository.pagination.CustomerDtoPagination;
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
    public static CustomerCreateDto createDto() {
        return new CustomerCreateDto(
                "Teste",
                "07021050070",
                "55210568972",
                "teste@email.com",
                "Address111",
                LocalDate.of(2024, 8, 7),
                "M"
        );
    }

    public static Customer client() {
        return new Customer(
                1L, "Test",
                "07021050070",
                "55210568972",
                "teste@email.com",
                "Address1",
                LocalDate.of(2024, 8, 7),
                Customer.Gender.M,
                LocalDateTime.now()
        );
    }

    public static CustomerResponseDto clientResponseDto() {
        return new CustomerResponseDto(
                "Test",
                "07021050070",
                "55210568972",
                "teste@email.com",
                "Address1",
                LocalDate.of(2024, 8, 7),
                "M"
        );
    }

    public static Page<CustomerDtoPagination> page() {
        Pageable pageable = PageRequest.of(0, 10);
        List<CustomerDtoPagination> content = List.of(
                new CustomerDtoPagination() {
                    public String getName() { return "Test"; }
                    public String getCpf() { return "07021050070"; }
                    public String getPhone() { return "55210568972"; }
                    public String getEmail() { return "teste@email.com"; }
                    public String getAddress() { return "Address1"; }
                    public LocalDate getBirthDate() { return LocalDate.of(2024, 8, 7); }
                    public String getGender() { return "M"; }
                }
        );
        return new PageImpl<>(content, pageable, content.size());
    }

    public static PageableDto pageableDto() {
        List<CustomerDtoPagination> content = List.of(
                new CustomerDtoPagination() {
                    public String getName() { return "Test"; }
                    public String getCpf() { return "07021050070"; }
                    public String getPhone() { return "55210568972"; }
                    public String getEmail() { return "teste@email.com"; }
                    public String getAddress() { return "Address1"; }
                    public LocalDate getBirthDate() { return LocalDate.of(2024, 8, 7); }
                    public String getGender() { return "M"; }
                }
        );
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);
        return pageableDto;
    }

    public static Page<CustomerDtoPagination> pageEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        List<CustomerDtoPagination> content = List.of();
        return new PageImpl<>(content, pageable, content.size());
    }

    public static PageableDto pageableDtoEmpty() {
        List<CustomerDtoPagination> content = List.of();
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);
        return pageableDto;
    }

    public static CustomerUpdateDto clientUpdateDto() {
        return new CustomerUpdateDto(
                "Test",
                "07021050070",
                "55210568972",
                "teste@email.com",
                "Address100",
                LocalDate.of(2024, 8, 7),
                "M"
        );
    }
}
