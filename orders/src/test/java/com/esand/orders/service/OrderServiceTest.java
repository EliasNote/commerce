//package com.esand.orders.service;
//
//import com.esand.orders.client.customers.Customer;
//import com.esand.orders.client.customers.CustomerClient;
//import com.esand.orders.client.products.Product;
//import com.esand.orders.client.products.ProductClient;
//import com.esand.orders.entity.EntityMock;
//import com.esand.orders.entity.Order;
//import com.esand.orders.exception.ConnectionException;
//import com.esand.orders.exception.EntityNotFoundException;
//import com.esand.orders.exception.InvalidQuantityException;
//import com.esand.orders.exception.UnavailableProductException;
//import com.esand.orders.repository.order.OrderRepository;
//import com.esand.orders.repository.pagination.OrderDtoPagination;
//import com.esand.orders.web.dto.OrderCreateDto;
//import com.esand.orders.web.dto.OrderResponseDto;
//import com.esand.orders.web.dto.PageableDto;
//import com.esand.orders.web.mapper.OrderMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.web.client.HttpClientErrorException;
//
//import java.io.Serializable;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//public class OrderServiceTest {
//
//    @Value("${topic_name}")
//    private String topicName;
//
//    @Mock
//    private OrderRepository orderRepository;
//
//    @Mock
//    private OrderMapper orderMapper;
//
//    @Mock
//    private ProductClient productCustomer;
//
//    @Mock
//    private CustomerClient customerClient;
//
//    @Mock
//    private KafkaTemplate<String, Serializable> kafkaTemplate;
//
//    @InjectMocks
//    private OrderService orderService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testSaveOrderSuccess() {
//        Customer customer = EntityMock.customer();
//        Product product = EntityMock.product();
//        Order order = EntityMock.order();
//        OrderResponseDto orderResponseDto = EntityMock.responseDto();
//        OrderCreateDto orderCreateDto = EntityMock.createDto();
//
//        when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
//        when(productCustomer.getProductBySku(any(String.class))).thenReturn(product);
//        when(orderMapper.toOrder(any(Customer.class), any(Product.class))).thenReturn(order);
//        when(orderRepository.save(any(Order.class))).thenReturn(order);
//        when(orderMapper.toDto(any(Order.class))).thenReturn(orderResponseDto);
//
//        OrderResponseDto response = orderService.save(orderCreateDto);
//
//        assertNotNull(response);
//        assertEquals(1L, response.getId());
//        assertEquals("John Doe", response.getName());
//        assertEquals("07021050070", response.getCpf());
//        assertEquals("Wireless Mouse", response.getTitle());
//        assertEquals("MOUSE-2024-WL-0010", response.getSku());
//        assertEquals(29.99, response.getPrice());
//        assertEquals(10, response.getQuantity());
//        assertEquals(299.9, response.getTotal());
//        assertFalse(response.getProcessing());
//        assertNotNull(response.getDate());
//    }
//
//    @Test
//    void testSaveOrderInvalidQuantityException() {
//        Product product = EntityMock.product();
//        OrderCreateDto orderCreateDto = EntityMock.createDto();
//
//        when(productCustomer.getProductBySku(anyString())).thenReturn(product);
//        orderCreateDto.setQuantity(0);
//
//        InvalidQuantityException exception = assertThrows(InvalidQuantityException.class, () -> {
//            orderService.save(orderCreateDto);
//        });
//
//        assertEquals("No quantity stated", exception.getMessage());
//    }
//
//    @Test
//    void testSaveOrderUnavailableQuantityException() {
//        Product product = EntityMock.product();
//        OrderCreateDto orderCreateDto = EntityMock.createDto();
//        orderCreateDto.setQuantity(11);
//
//        when(productCustomer.getProductBySku(anyString())).thenReturn(product);
//
//        InvalidQuantityException exception = assertThrows(InvalidQuantityException.class, () -> {
//            orderService.save(orderCreateDto);
//        });
//
//        assertEquals("The quantity of available products is " + product.getQuantity(), exception.getMessage());
//    }
//
//    @Test
//    void testSaveOrderUnavailableProductException() {
//        Product product = EntityMock.product();
//        product.setStatus(false);
//        OrderCreateDto orderCreateDto = EntityMock.createDto();
//
//        when(productCustomer.getProductBySku(anyString())).thenReturn(product);
//
//        UnavailableProductException exception = assertThrows(UnavailableProductException.class, () -> {
//            orderService.save(orderCreateDto);
//        });
//
//        assertEquals("The product is not available", exception.getMessage());
//    }
//
//    @Test
//    void testSaveOrderNotFoundProductException() {
//        OrderCreateDto orderCreateDto = EntityMock.createDto();
//
//        HttpClientErrorException.NotFound notFoundException = mock(HttpClientErrorException.NotFound.class);
//        when(productCustomer.getProductBySku(anyString())).thenThrow(notFoundException);
//        when(notFoundException.getMessage()).thenReturn("Product not found by sku");
//
//        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
//            orderService.save(orderCreateDto);
//        });
//
//        assertEquals("Product not found by SKU", exception.getMessage());
//    }
//
//    @Test
//    void testSaveOrderNotFoundCustomerException() {
//        OrderCreateDto orderCreateDto = EntityMock.createDto();
//
//        HttpClientErrorException.NotFound notFoundException = mock(HttpClientErrorException.NotFound.class);
//        when(customerClient.getCustomerByCpf(anyString())).thenThrow(notFoundException);
//        when(notFoundException.getMessage()).thenReturn("Customer not found by CPF");
//
//        assertThrows(EntityNotFoundException.class, () -> {
//            orderService.save(orderCreateDto);
//        });
//    }
//
//    @Test
//    void testSaveOrderServiceUnavailableProductException() {
//        OrderCreateDto orderCreateDto = EntityMock.createDto();
//
//        ConnectionException connectionException = mock(ConnectionException.class);
//        when(productCustomer.getProductBySku(anyString())).thenThrow(connectionException);
//        when(connectionException.getMessage()).thenReturn("Service unavailable");
//
//        ConnectionException exception = assertThrows(ConnectionException.class, () -> {
//            orderService.save(orderCreateDto);
//        });
//
//        assertEquals("Service unavailable", exception.getMessage());
//    }
//
//    @Test
//    void testSaveOrderServiceUnavailableCustomerException() {
//        OrderCreateDto orderCreateDto = EntityMock.createDto();
//
//        ConnectionException connectionException = mock(ConnectionException.class);
//        when(customerClient.getCustomerByCpf(anyString())).thenThrow(connectionException);
//        when(connectionException.getMessage()).thenReturn("Service unavailable");
//
//        ConnectionException exception = assertThrows(ConnectionException.class, () -> {
//            orderService.save(orderCreateDto);
//        });
//
//        assertEquals("Service unavailable", exception.getMessage());
//    }
//
//    @Test
//    void testFindAllSuccess() {
//        Page<OrderDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(orderRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
//        when(orderMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = orderService.findAll(null, null, page.getPageable());
//
//        assertNotNull(response);
//        assertNotNull(response.getContent());
//        assertEquals(1, response.getContent().size());
//
//        OrderDtoPagination order = (OrderDtoPagination) response.getContent().get(0);
//        assertEquals(page.getContent().get(0).getId(), order.getId());
//        assertEquals(page.getContent().get(0).getName(), order.getName());
//        assertEquals(page.getContent().get(0).getCpf(), order.getCpf());
//        assertEquals(page.getContent().get(0).getTitle(), order.getTitle());
//        assertEquals(page.getContent().get(0).getSku(), order.getSku());
//        assertEquals(page.getContent().get(0).getPrice(), order.getPrice());
//        assertEquals(page.getContent().get(0).getQuantity(), order.getQuantity());
//        assertEquals(page.getContent().get(0).getTotal(), order.getTotal());
//        assertFalse(order.getProcessing());
//        assertNotNull(order.getDate());
//    }
//
//    @Test
//    void testFindAllEntityNotFoundException() {
//        Page<OrderDtoPagination> page = EntityMock.pageEmpty();
//        PageableDto pageableDto = EntityMock.pageableDtoEmpty();
//
//        when(orderRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
//        when(orderMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        assertThrows(EntityNotFoundException.class, () -> orderService.findAll(null, null, page.getPageable()));
//    }
//
//    @Test
//    void testFindBySkuSuccess() {
//        Page<OrderDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(orderRepository.findBySku(any(Pageable.class), any(String.class))).thenReturn(page);
//        when(orderMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = orderService.findBySku(page.getPageable(), page.getContent().get(0).getSku());
//
//        assertNotNull(response);
//        assertNotNull(response.getContent());
//        assertEquals(1, response.getContent().size());
//
//        OrderDtoPagination order = (OrderDtoPagination) response.getContent().get(0);
//        assertEquals(page.getContent().get(0).getId(), order.getId());
//        assertEquals(page.getContent().get(0).getName(), order.getName());
//        assertEquals(page.getContent().get(0).getCpf(), order.getCpf());
//        assertEquals(page.getContent().get(0).getTitle(), order.getTitle());
//        assertEquals(page.getContent().get(0).getSku(), order.getSku());
//        assertEquals(page.getContent().get(0).getPrice(), order.getPrice());
//        assertEquals(page.getContent().get(0).getQuantity(), order.getQuantity());
//        assertEquals(page.getContent().get(0).getTotal(), order.getTotal());
//        assertFalse(order.getProcessing());
//        assertNotNull(order.getDate());
//    }
//
//    @Test
//    void testFindBySkuEntityNotFoundException() {
//        Page<OrderDtoPagination> page = EntityMock.pageEmpty();
//        PageableDto pageableDto = EntityMock.pageableDtoEmpty();
//
//        when(orderRepository.findBySku(any(Pageable.class), any(String.class))).thenReturn(page);
//        when(orderMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
//                orderService.findBySku(page.getPageable(), "MOUSE-2024-WL-0010")
//        );
//
//        assertEquals("No orders found by sku", exception.getMessage());
//    }
//
//    @Test
//    void testFindByCpfSuccess() {
//        Page<OrderDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(orderRepository.findByCpf(any(Pageable.class), any(String.class))).thenReturn(page);
//        when(orderMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = orderService.findByCpf(page.getPageable(), page.getContent().get(0).getCpf());
//
//        assertNotNull(response);
//        assertNotNull(response.getContent());
//        assertEquals(1, response.getContent().size());
//
//        OrderDtoPagination order = (OrderDtoPagination) response.getContent().get(0);
//        assertEquals(page.getContent().get(0).getId(), order.getId());
//        assertEquals(page.getContent().get(0).getName(), order.getName());
//        assertEquals(page.getContent().get(0).getCpf(), order.getCpf());
//        assertEquals(page.getContent().get(0).getTitle(), order.getTitle());
//        assertEquals(page.getContent().get(0).getCpf(), order.getCpf());
//        assertEquals(page.getContent().get(0).getPrice(), order.getPrice());
//        assertEquals(page.getContent().get(0).getQuantity(), order.getQuantity());
//        assertEquals(page.getContent().get(0).getTotal(), order.getTotal());
//        assertFalse(order.getProcessing());
//        assertNotNull(order.getDate());
//    }
//
//    @Test
//    void testFindByCpfEntityNotFoundException() {
//        Page<OrderDtoPagination> page = EntityMock.pageEmpty();
//        PageableDto pageableDto = EntityMock.pageableDtoEmpty();
//
//        when(orderRepository.findByCpf(any(Pageable.class), any(String.class))).thenReturn(page);
//        when(orderMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
//                orderService.findByCpf(page.getPageable(), "MOUSE-2024-WL-0010")
//        );
//
//        assertEquals("No orders found by cpf", exception.getMessage());
//    }
//
//    @Test
//    void testDeleteOrderByIdSuccess() {
//        when(orderRepository.existsById(any(Long.class))).thenReturn(true);
//        doNothing().when(orderRepository).deleteById(any(Long.class));
//
//        orderService.delete(1L);
//
//        verify(orderRepository).deleteById(1L);
//    }
//
//    @Test
//    void testDeleteOrderByIdEntityNotFoundException() {
//        when(orderRepository.existsById(any(Long.class))).thenReturn(false);
//
//        assertThrows(EntityNotFoundException.class, () -> orderService.delete(1L));
//    }
//
//    @Test
//    void testDeleteAllProcessingOrdersSuccess() {
//        when(orderRepository.existsByProcessing(eq(true))).thenReturn(true);
//        doNothing().when(orderRepository).deleteAllByProcessing(eq(true));
//
//        orderService.deleteAllProcessing();
//
//        verify(orderRepository).deleteAllByProcessing(true);
//    }
//
//    @Test
//    void testDeleteAllProcessingOrdersEntityNotFoundException() {
//        when(orderRepository.existsByProcessing(eq(true))).thenReturn(false);
//
//        assertThrows(EntityNotFoundException.class, () -> orderService.deleteAllProcessing());
//    }
//}
