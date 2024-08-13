package com.esand.clients.entity;

import com.esand.clients.repository.pagination.ClientDtoPagination;
import com.esand.clients.web.dto.ClientCreateDto;
import com.esand.clients.web.dto.ClientResponseDto;
import com.esand.clients.web.dto.ClientUpdateDto;
import com.esand.clients.web.dto.PageableDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class EntityMock {
    public static ClientCreateDto createDto() {
        return new ClientCreateDto(
                "Teste",
                "07021050070",
                "55210568972",
                "teste@email.com",
                "Address111",
                LocalDate.of(2024, 8, 7),
                "M"
        );
    }

    public static Client client() {
        return new Client(
                1L, "Test",
                "07021050070",
                "55210568972",
                "teste@email.com",
                "Address1",
                LocalDate.of(2024, 8, 7),
                Client.Gender.M,
                LocalDateTime.now()
        );
    }

    public static ClientResponseDto clientResponseDto() {
        return new ClientResponseDto(
                "Test",
                "07021050070",
                "55210568972",
                "teste@email.com",
                "Address1",
                LocalDate.of(2024, 8, 7),
                "M"
        );
    }

    public static Page<ClientDtoPagination> page() {
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
        return new PageImpl<>(content, pageable, content.size());
    }

    public static PageableDto pageableDto() {
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
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);
        return pageableDto;
    }

    public static Page<ClientDtoPagination> pageEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ClientDtoPagination> content = List.of();
        return new PageImpl<>(content, pageable, content.size());
    }

    public static PageableDto pageableDtoEmpty() {
        List<ClientDtoPagination> content = List.of();
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);
        return pageableDto;
    }

    public static ClientUpdateDto clientUpdateDto() {
        return new ClientUpdateDto(
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
