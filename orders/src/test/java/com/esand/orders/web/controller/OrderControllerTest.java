package com.esand.orders.web.controller;

import com.esand.orders.entity.EntityMock;
import com.esand.orders.exception.EntityNotFoundException;
import com.esand.orders.service.OrderService;
import com.esand.orders.web.dto.OrderCreateDto;
import com.esand.orders.web.dto.OrderResponseDto;
import com.esand.orders.web.dto.PageableDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper(); // Inicialize o ObjectMapper aqui
    }

    void verifyResult(Object object, OrderResponseDto dto) throws Exception {
        OrderResponseDto response;

        if (object instanceof PageableDto) {
            PageableDto page = (PageableDto) object;
            assertNotNull(page);
            assertNotNull(page.getContent());
            assertEquals(1, page.getContent().size());

            response = (OrderResponseDto) page.getContent().get(0);
        } else {
            response = (OrderResponseDto) object;
        }

        assertNotNull(response);
        assertEquals(dto.getId(), response.getId());
        assertEquals(dto.getName(), response.getName());
        assertEquals(dto.getCpf(), response.getCpf());
        assertEquals(dto.getTitle(), response.getTitle());
        assertEquals(dto.getSku(), response.getSku());
        assertEquals(dto.getPrice(), response.getPrice());
        assertEquals(dto.getQuantity(), response.getQuantity());
        assertEquals(dto.getTotal(), response.getTotal());
        assertEquals(dto.getProcessing(), response.getProcessing());
        assertNotNull(response.getDate());
    }

    @Test
    void testSaveOrderSuccess() throws Exception {
        OrderResponseDto orderResponseDto = EntityMock.responseDto();
        OrderCreateDto orderCreateDto = EntityMock.createDto();

        when(orderService.save(orderCreateDto)).thenReturn(orderResponseDto);

        ResponseEntity<OrderResponseDto> response = orderController.create(orderCreateDto);

        verifyResult(response.getBody(), EntityMock.responseDto());
    }

//    @Test
//    void testCreateOrderCustomerNotFoundException() throws Exception {
//        OrderCreateDto orderCreateDto = EntityMock.createDto();
//
//        when(orderService.save(any(OrderCreateDto.class)))
//                .thenThrow(new EntityNotFoundException("Customer not found by CPF"));
//
//        String json = objectMapper.writeValueAsString(orderCreateDto);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json))
//                .andExpect(status().isNotFound())
//                .andExpect(jsonPath("$.message").value("Customer not found by CPF"));
//    }
}