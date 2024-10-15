package com.esand.orders.entity;

import com.esand.orders.client.customers.Customer;
import com.esand.orders.client.products.Category;
import com.esand.orders.client.products.Product;
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

    public static String CUSTOMER_CPF = "07021050070";
    public static String CUSTOMER_NAME = "John Doe";
    public static String CUSTOMER_PHONE = "55210568972";
    public static String CUSTOMER_EMAIL = "teste@email.com";
    public static String CUSTOMER_ADDRESS = "Address1";
    public static String CUSTOMER_GENDER = "M";
    public static LocalDate CUSTOMER_BIRTHDATE = LocalDate.of(2024, 8, 7);

    public static String PRODUCT_TITLE = "Wireless Mouse";
    public static String PRODUCT_SKU = "MOUSE-2024-WL-0010";
    public static Integer PRODUCT_QUANTITY = 10;
    public static Double PRODUCT_PRICE = 29.99;
    public static String PRODUCT_DESCRIPTION = "A high precision wireless m";
    public static String PRODUCT_CATEGORY = "MOUSES";
    public static Boolean PRODUCT_STATUS = true;

    public static Boolean ORDER_PROCESSING = false;

    public static OrderCreateDto createDto() {
        return new OrderCreateDto(
                CUSTOMER_CPF,
                PRODUCT_SKU,
                PRODUCT_QUANTITY
        );
    }

    public static OrderResponseDto responseDto() {
        return new OrderResponseDto(
                1L,
                CUSTOMER_NAME,
                CUSTOMER_CPF,
                PRODUCT_TITLE,
                PRODUCT_SKU,
                PRODUCT_PRICE,
                PRODUCT_QUANTITY,
                PRODUCT_QUANTITY * PRODUCT_PRICE,
                ORDER_PROCESSING,
                LocalDateTime.now()
        );
    }

    public static Page<OrderResponseDto> page() {
        Pageable pageable = PageRequest.of(0, 10);
        List<OrderResponseDto> content = List.of(
                responseDto()
        );
        return new PageImpl<>(content, pageable, content.size());
    }

    public static Page<OrderResponseDto> pageEmpty() {
        Pageable pageable = PageRequest.of(0, 10);
        List<OrderResponseDto> content = List.of();
        return new PageImpl<>(content, pageable, content.size());
    }

    public static PageableDto pageableDto() {
        List<OrderResponseDto> content = List.of(
                new OrderResponseDto() {
                    public Long getId() { return 1L; }
                    public String getName() { return CUSTOMER_NAME; }
                    public String getCpf() { return CUSTOMER_CPF; }
                    public String getTitle() { return PRODUCT_TITLE; }
                    public String getSku() { return PRODUCT_SKU; }
                    public Double getPrice() { return PRODUCT_PRICE; }
                    public Integer getQuantity() { return PRODUCT_QUANTITY; }
                    public Double getTotal() { return PRODUCT_QUANTITY * PRODUCT_PRICE; }
                    public Boolean getProcessing() { return ORDER_PROCESSING; }
                    public LocalDateTime getDate() { return LocalDateTime.now(); }
                }
        );
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);
        return pageableDto;
    }

    public static PageableDto pageableDtoEmpty() {
        List<OrderResponseDto> content = List.of();
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);
        return pageableDto;
    }

    public static Order order() {
        return new Order(
                1L,
                PRODUCT_SKU,
                CUSTOMER_CPF,
                PRODUCT_PRICE,
                PRODUCT_QUANTITY,
                PRODUCT_QUANTITY * PRODUCT_PRICE,
                ORDER_PROCESSING,
                LocalDateTime.now()
        );
    }

    public static Customer customer() {
        return new Customer(
                CUSTOMER_NAME,
                CUSTOMER_CPF,
                CUSTOMER_PHONE,
                CUSTOMER_EMAIL,
                CUSTOMER_ADDRESS,
                CUSTOMER_BIRTHDATE,
                CUSTOMER_GENDER
        );
    }

    public static Product product() {
        return new Product(
                PRODUCT_TITLE,
                PRODUCT_DESCRIPTION,
                PRODUCT_PRICE,
                List.of(new Category(PRODUCT_CATEGORY)),
                PRODUCT_QUANTITY,
                PRODUCT_SKU,
                PRODUCT_STATUS
        );
    }
}
