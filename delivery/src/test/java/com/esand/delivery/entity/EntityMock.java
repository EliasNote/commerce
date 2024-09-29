package com.esand.delivery.entity;

import com.esand.delivery.repository.pagination.DeliveryDtoPagination;
import com.esand.delivery.web.dto.DeliveryResponseDto;
import com.esand.delivery.web.dto.DeliverySaveDto;
import com.esand.delivery.web.dto.PageableDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public class EntityMock {

    public static Long DELIVERY_ID = 1L;
    public static String DELIVERY_STATUS = "PROCESSING";

    public static String CUSTOMER_NAME = "John Doe";
    public static String CUSTOMER_CPF = "07021050070";

    public static String PRODUCT_TITLE = "Wireless Mouse";
    public static String PRODUCT_SKU = "MOUSE-2024-WL-0010";
    public static Double PRODUCT_PRICE = 29.99;
    public static Integer PRODUCT_QUANTITY = 10;

    public static DeliverySaveDto saveDto() {
        return new DeliverySaveDto(
                DELIVERY_ID,
                CUSTOMER_NAME,
                CUSTOMER_CPF,
                PRODUCT_TITLE,
                PRODUCT_SKU,
                PRODUCT_PRICE,
                PRODUCT_QUANTITY,
                PRODUCT_QUANTITY * PRODUCT_PRICE,
                LocalDateTime.now()
        );
    }

    public static DeliveryResponseDto responseDto() {
        return new DeliveryResponseDto(
                DELIVERY_ID,
                CUSTOMER_NAME,
                CUSTOMER_CPF,
                PRODUCT_TITLE,
                PRODUCT_SKU,
                PRODUCT_PRICE,
                PRODUCT_QUANTITY,
                PRODUCT_QUANTITY * PRODUCT_PRICE,
                DELIVERY_STATUS,
                LocalDateTime.now()
        );
    }

    public static Page<DeliveryDtoPagination> page() {
        Pageable pageable = PageRequest.of(0, PRODUCT_QUANTITY);
        List<DeliveryDtoPagination> content = List.of(
                new DeliveryDtoPagination() {
                    public Long getId() { return DELIVERY_ID; }
                    public String getName() { return CUSTOMER_NAME; }
                    public String getCpf() { return CUSTOMER_CPF; }
                    public String getTitle() { return PRODUCT_TITLE; }
                    public String getSku() { return PRODUCT_SKU; }
                    public Double getPrice() { return PRODUCT_PRICE; }
                    public Integer getQuantity() { return PRODUCT_QUANTITY; }
                    public Double getTotal() { return PRODUCT_QUANTITY * PRODUCT_PRICE; }
                    public String getStatus() { return DELIVERY_STATUS; }
                    public LocalDateTime getDate() { return LocalDateTime.now(); }
                }
        );
        return new PageImpl<>(content, pageable, content.size());
    }

    public static Page<DeliveryDtoPagination> pageEmpty() {
        Pageable pageable = PageRequest.of(0, PRODUCT_QUANTITY);
        List<DeliveryDtoPagination> content = List.of();
        return new PageImpl<>(content, pageable, content.size());
    }

    public static PageableDto pageableDto() {
        List<DeliveryDtoPagination> content = List.of(
                new DeliveryDtoPagination() {
                    public Long getId() { return DELIVERY_ID; }
                    public String getName() { return CUSTOMER_NAME; }
                    public String getCpf() { return CUSTOMER_CPF; }
                    public String getTitle() { return PRODUCT_TITLE; }
                    public String getSku() { return PRODUCT_SKU; }
                    public Double getPrice() { return PRODUCT_PRICE; }
                    public Integer getQuantity() { return PRODUCT_QUANTITY; }
                    public Double getTotal() { return PRODUCT_QUANTITY * PRODUCT_PRICE; }
                    public String getStatus() { return DELIVERY_STATUS; }
                    public LocalDateTime getDate() { return LocalDateTime.now(); }
                }
        );
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);
        return pageableDto;
    }

    public static PageableDto pageableDtoEmpty() {
        List<DeliveryDtoPagination> content = List.of();
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);
        return pageableDto;
    }

    public static Delivery delivery() {
        return new Delivery(
                DELIVERY_ID,
                PRODUCT_TITLE,
                PRODUCT_SKU,
                CUSTOMER_NAME,
                CUSTOMER_CPF,
                PRODUCT_PRICE,
                PRODUCT_QUANTITY,
                PRODUCT_QUANTITY * PRODUCT_PRICE,
                Delivery.Status.PROCESSING,
                LocalDateTime.now()
        );
    }
}