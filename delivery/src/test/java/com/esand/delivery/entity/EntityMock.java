//package com.esand.delivery.entity;
//
//import com.esand.delivery.repository.pagination.DeliveryDtoPagination;
//import com.esand.delivery.web.dto.DeliveryResponseDto;
//import com.esand.delivery.web.dto.DeliverySaveDto;
//import com.esand.delivery.web.dto.PageableDto;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//public class EntityMock {
//    public static DeliverySaveDto saveDto() {
//        return new DeliverySaveDto(
//                1L,
//                "John Doe",
//                "07021050070",
//                "Wireless Mouse",
//                "MOUSE-2024-WL-0010",
//                29.99,
//                10,
//                299.9,
//                LocalDateTime.now()
//        );
//    }
//
//    public static DeliveryResponseDto responseDto() {
//        return new DeliveryResponseDto(
//                1L,
//                "John Doe",
//                "07021050070",
//                "Wireless Mouse",
//                "MOUSE-2024-WL-0010",
//                29.99,
//                10,
//                299.9,
//                "PROCESSING",
//                LocalDateTime.now()
//        );
//    }
//
//    public static Page<DeliveryDtoPagination> page() {
//        Pageable pageable = PageRequest.of(0, 10);
//        List<DeliveryDtoPagination> content = List.of(
//                new DeliveryDtoPagination() {
//                    public Long getId() { return 1L; }
//                    public String getName() { return "John Doe"; }
//                    public String getCpf() { return "07021050070"; }
//                    public String getTitle() { return "Wireless Mouse"; }
//                    public String getSku() { return "MOUSE-2024-WL-0010"; }
//                    public Double getPrice() { return 29.99; }
//                    public Integer getQuantity() { return 10; }
//                    public Double getTotal() { return 299.9; }
//                    public String getStatus() { return "PROCESSING"; }
//                    public LocalDateTime getDate() { return LocalDateTime.now(); }
//                }
//        );
//        return new PageImpl<>(content, pageable, content.size());
//    }
//
//    public static Page<DeliveryDtoPagination> pageEmpty() {
//        Pageable pageable = PageRequest.of(0, 10);
//        List<DeliveryDtoPagination> content = List.of();
//        return new PageImpl<>(content, pageable, content.size());
//    }
//
//    public static PageableDto pageableDto() {
//        List<DeliveryDtoPagination> content = List.of(
//                new DeliveryDtoPagination() {
//                    public Long getId() { return 1L; }
//                    public String getName() { return "John Doe"; }
//                    public String getCpf() { return "07021050070"; }
//                    public String getTitle() { return "Wireless Mouse"; }
//                    public String getSku() { return "MOUSE-2024-WL-0010"; }
//                    public Double getPrice() { return 29.99; }
//                    public Integer getQuantity() { return 10; }
//                    public Double getTotal() { return 299.9; }
//                    public String getStatus() { return "PROCESSING"; }
//                    public LocalDateTime getDate() { return LocalDateTime.now(); }
//                }
//        );
//        PageableDto pageableDto = new PageableDto();
//        pageableDto.setContent(content);
//        return pageableDto;
//    }
//
//    public static PageableDto pageableDtoEmpty() {
//        List<DeliveryDtoPagination> content = List.of();
//        PageableDto pageableDto = new PageableDto();
//        pageableDto.setContent(content);
//        return pageableDto;
//    }
//
//    public static Delivery delivery() {
//        return new Delivery(
//                1L,
//                "Wireless Mouse",
//                "MOUSE-2024-WL-0010",
//                "John Doe",
//                "07021050070",
//                29.99,
//                10,
//                299.9,
//                Delivery.Status.PROCESSING,
//                LocalDateTime.now()
//        );
//    }
//}