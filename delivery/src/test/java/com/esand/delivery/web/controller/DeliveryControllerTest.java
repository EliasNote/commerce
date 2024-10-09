//package com.esand.delivery.web.controller;
//
//import com.esand.delivery.entity.Delivery;
//import com.esand.delivery.entity.EntityMock;
//import com.esand.delivery.exception.EntityNotFoundException;
//import com.esand.delivery.repository.pagination.DeliveryDtoPagination;
//import com.esand.delivery.service.DeliveryService;
//import com.esand.delivery.web.dto.DeliveryResponseDto;
//import com.esand.delivery.web.dto.DeliverySaveDto;
//import com.esand.delivery.web.dto.PageableDto;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.internal.matchers.Null;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.isNull;
//import static org.mockito.Mockito.when;
//
//class DeliveryControllerTest {
//    @Mock
//    private DeliveryService deliveryService;
//
//    @InjectMocks
//    private DeliveryController deliveryController;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    void verifyResult(ResponseEntity<PageableDto> response, ResponseEntity<DeliveryResponseDto> responseDto, Delivery delivery) {
//        if (response != null) {
//            DeliveryDtoPagination content = (DeliveryDtoPagination) response.getBody().getContent().get(0);
//            assertEquals(delivery.getId(), content.getId());
//            assertEquals(delivery.getName(), content.getName());
//            assertEquals(delivery.getCpf(), content.getCpf());
//            assertEquals(delivery.getTitle(), content.getTitle());
//            assertEquals(delivery.getSku(), content.getSku());
//            assertEquals(delivery.getPrice(), content.getPrice());
//            assertEquals(delivery.getQuantity(), content.getQuantity());
//            assertEquals(delivery.getTotal(), content.getTotal());
//            assertEquals(delivery.getStatus().toString(), content.getStatus());
//            assertNotNull(content.getDate());
//        } else {
//            DeliveryResponseDto content = responseDto.getBody();
//            assertEquals(delivery.getId(), content.getId());
//            assertEquals(delivery.getName(), content.getName());
//            assertEquals(delivery.getCpf(), content.getCpf());
//            assertEquals(delivery.getTitle(), content.getTitle());
//            assertEquals(delivery.getSku(), content.getSku());
//            assertEquals(delivery.getPrice(), content.getPrice());
//            assertEquals(delivery.getQuantity(), content.getQuantity());
//            assertEquals(delivery.getTotal(), content.getTotal());
//            assertEquals(delivery.getStatus().toString(), content.getStatus());
//            assertNotNull(content.getDate());
//        }
//
//    }
//
//    @Test
//    void testFindAllDeliverySuccess() {
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryService.findAll(isNull(), isNull(), any(Pageable.class))).thenReturn(pageableDto);
//
//        ResponseEntity<PageableDto> response = deliveryController.findAll(EntityMock.page().getPageable(), null, null);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        verifyResult(response, null, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindAllByDateBetweenDeliverySuccess() {
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryService.findAll(any(String.class), any(String.class), any(Pageable.class))).thenReturn(pageableDto);
//
//        ResponseEntity<PageableDto> response = deliveryController.findAll(EntityMock.page().getPageable(), LocalDate.now().minusDays(1).toString(), LocalDate.now().plusDays(1).toString());
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        verifyResult(response, null, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindAllDeliveryEntityNotFoundException() {
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryService.findAll(isNull(), isNull(), any(Pageable.class))).thenThrow(new EntityNotFoundException("No orders found"));
//
//        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
//            deliveryController.findAll(EntityMock.page().getPageable(), null, null);
//        });
//
//        assertEquals("No orders found", thrown.getMessage());
//    }
//
//    @Test
//    void testFindByIdSuccess() {
//        DeliveryResponseDto responseDto = EntityMock.responseDto();
//
//        when(deliveryService.findById(any(Long.class))).thenReturn(responseDto);
//
//        ResponseEntity<DeliveryResponseDto> response = deliveryController.findById(EntityMock.DELIVERY_ID);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        verifyResult(null, response, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindByIdEntityNotFoundException() {
//        DeliveryResponseDto responseDto = EntityMock.responseDto();
//
//        when(deliveryService.findById(any(Long.class))).thenThrow(new EntityNotFoundException("Order nº" + EntityMock.DELIVERY_ID + " does not exist"));
//
//        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
//            deliveryController.findById(EntityMock.DELIVERY_ID);
//        });
//
//        assertEquals("Order nº" + EntityMock.DELIVERY_ID + " does not exist", thrown.getMessage());
//    }
//}