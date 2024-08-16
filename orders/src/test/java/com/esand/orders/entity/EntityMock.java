package com.esand.orders.entity;

import com.esand.orders.client.clients.Client;
import com.esand.orders.client.products.Product;
import com.esand.orders.repository.pagination.OrderDtoPagination;
import com.esand.orders.web.dto.OrderCreateDto;
import com.esand.orders.web.dto.OrderResponseDto;
import com.esand.orders.web.dto.PageableDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class EntityMock {

    public static OrderCreateDto createDto() {
        return new OrderCreateDto(
                "07021050070",
                "MOUSE-2024-WL-0010",
                10
        );
    }

    public static OrderResponseDto responseDto() {
        return new OrderResponseDto(
                1L,
                "John Doe",
                "07021050070",
                "Wireless Mouse",
                "MOUSE-2024-WL-0010",
                29.99,
                10,
                299.9,
                false,
                LocalDateTime.now()
        );
    }

    public static Page<OrderDtoPagination> page() {
        Pageable pageable = PageRequest.of(0, 10);
        List<OrderDtoPagination> content = List.of(
                new OrderDtoPagination() {
                    public Long getId() { return 1L; }
                    public String getName() { return "John Doe"; }
                    public String getCpf() { return "07021050070"; }
                    public String getTitle() { return "Wireless Mouse"; }
                    public String getSku() { return "MOUSE-2024-WL-0010"; }
                    public Double getPrice() { return 29.99; }
                    public Integer getQuantity() { return 10; }
                    public Double getTotal() { return 299.9; }
                    public Boolean getProcessing() { return false; }
                    public LocalDateTime getDate() { return LocalDateTime.now(); }
                }
        );
        return new PageImpl<>(content, pageable, content.size());
    }

    public static Page<OrderDtoPagination> pageEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        List<OrderDtoPagination> content = List.of();
        return new PageImpl<>(content, pageable, content.size());
    }

    public static PageableDto pageableDto() {
        List<OrderDtoPagination> content = List.of(
                new OrderDtoPagination() {
                    public Long getId() { return 1L; }
                    public String getName() { return "John Doe"; }
                    public String getCpf() { return "07021050070"; }
                    public String getTitle() { return "Wireless Mouse"; }
                    public String getSku() { return "MOUSE-2024-WL-0010"; }
                    public Double getPrice() { return 29.99; }
                    public Integer getQuantity() { return 10; }
                    public Double getTotal() { return 299.9; }
                    public Boolean getProcessing() { return false; }
                    public LocalDateTime getDate() { return LocalDateTime.now(); }
                }
        );
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);
        return pageableDto;
    }

    public static PageableDto pageableDtoEmpty() {
        List<OrderDtoPagination> content = List.of();
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);
        return pageableDto;
    }

    public static Order order() {
        return new Order(
                1L,
                "Wireless Mouse",
                "MOUSE-2024-WL-0010",
                "John Doe",
                "07021050070",
                29.99,
                10,
                299.9,
                false,
                LocalDateTime.now()
        );
    }

    public static Client client() {
        return new Client(
                "John Doe",
                "07021050070",
                "55210568972",
                "teste@email.com",
                "Address1",
                LocalDate.of(2024, 8, 7),
                "M"
        );
    }

    public static Product product() {
        return new Product(
                "Wireless Mouse",
                "A high precision wireless m",
                29.99,
                "MOUSES",
                10,
                "MOUSE-2024-WL-0010",
                true
        );
    }
}
