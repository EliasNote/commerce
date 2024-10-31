//package com.esand.customers.web.controller;
//
//import com.esand.customers.service.CustomerService;
//import com.esand.customers.web.dto.CustomerCreateDto;
//import com.esand.customers.web.dto.CustomerResponseDto;
//import com.esand.customers.web.dto.CustomerUpdateDto;
//import com.esand.customers.web.dto.PageableDto;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//
//
//class CustomerControllerTest {
//    @Mock
//    private CustomerService customerService;
//
//    @InjectMocks
//    private CustomerController customerController;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void create() {
//        CustomerCreateDto createDto =  new CustomerCreateDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");
//        CustomerResponseDto responseDto = new CustomerResponseDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");
//
//        when(customerService.save(any(CustomerCreateDto.class))).thenReturn(responseDto);
//
//        ResponseEntity<CustomerResponseDto> response = customerController.create(createDto);
//
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("Test", response.getBody().getName());
//        assertEquals("07021050070", response.getBody().getCpf());
//        assertEquals("55210568972", response.getBody().getPhone());
//        assertEquals("teste@email.com", response.getBody().getEmail());
//        assertEquals("Address1", response.getBody().getAddress());
//        assertEquals(LocalDate.of(2024, 8, 7), response.getBody().getBirthDate());
//        assertEquals("M", response.getBody().getGender());
//    }
//
//    @Test
//    void findAll() {
//        CustomerCreateDto createDto =  new CustomerCreateDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");
//        Pageable pageable = PageRequest.of(0, 10);
//        PageableDto pageableDto = new PageableDto();
//        pageableDto.setContent(List.of(createDto));
//
//        when(customerService.findAll(null, null, pageable)).thenReturn(pageableDto);
//
//        ResponseEntity<PageableDto> response = customerController.findAll(pageable, null, null);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertNotNull(response.getBody().getContent());
//        assertEquals(1, response.getBody().getContent().size());
//    }
//
//    @Test
//    void findByName() {
//        CustomerCreateDto createDto =  new CustomerCreateDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");
//        Pageable pageable = PageRequest.of(0, 10);
//        PageableDto pageableDto = new PageableDto();
//        pageableDto.setContent(List.of(createDto));
//
//        when(customerService.findByName(pageable, "Test")).thenReturn(pageableDto);
//
//        ResponseEntity<PageableDto> response = customerController.findByName(pageable, "Test");
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertNotNull(response.getBody().getContent());
//        assertEquals(1, response.getBody().getContent().size());
//    }
//
//    @Test
//    void findByCpf() {
//        CustomerResponseDto responseDto =  new CustomerResponseDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");
//
//        when(customerService.findByCpf("07021050070")).thenReturn(responseDto);
//
//        ResponseEntity<CustomerResponseDto> response = customerController.findByCpf("07021050070");
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertNotNull(response.getBody());
//        assertEquals("Test", response.getBody().getName());
//        assertEquals("07021050070", response.getBody().getCpf());
//        assertEquals("55210568972", response.getBody().getPhone());
//        assertEquals("teste@email.com", response.getBody().getEmail());
//        assertEquals("Address1", response.getBody().getAddress());
//        assertEquals(LocalDate.of(2024, 8, 7), response.getBody().getBirthDate());
//        assertEquals("M", response.getBody().getGender());
//    }
//
////    @Test
////    void findByDate() {
////        CustomerCreateDto createDto =  new CustomerCreateDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");
////        Pageable pageable = PageRequest.of(0, 10);
////        PageableDto pageableDto = new PageableDto();
////        pageableDto.setContent(List.of(createDto));
////
////        when(customerService.findCustomersByDate("2024-08-08", "2024-08-08", pageable)).thenReturn(pageableDto);
////
////        ResponseEntity<PageableDto> response = customerController.findByDate(pageable, "2024-08-08", "2024-08-08");
////
////        assertEquals(HttpStatus.OK, response.getStatusCode());
////        assertNotNull(response.getBody());
////        assertNotNull(response.getBody().getContent());
////        assertEquals(1, response.getBody().getContent().size());
////    }
//
//    @Test
//    void update() {
//        CustomerUpdateDto updateDto =  new CustomerUpdateDto("Test", "07021050070", "55210568972", "teste@email.com", "Address1", LocalDate.of(2024, 8, 7), "M");
//
//        doNothing().when(customerService).update("07021050070", updateDto);
//
//        ResponseEntity<Void> response = customerController.update("07021050070", updateDto);
//
//        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
//    }
//}