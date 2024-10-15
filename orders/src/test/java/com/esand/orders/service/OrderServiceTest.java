package com.esand.orders.service;

import com.esand.orders.client.customers.Customer;
import com.esand.orders.client.customers.CustomerClient;
import com.esand.orders.client.products.Product;
import com.esand.orders.client.products.ProductClient;
import com.esand.orders.entity.EntityMock;
import com.esand.orders.entity.Order;
import com.esand.orders.exception.*;
import com.esand.orders.repository.order.OrderRepository;
import com.esand.orders.web.dto.OrderCreateDto;
import com.esand.orders.web.dto.OrderResponseDto;
import com.esand.orders.web.dto.PageableDto;
import com.esand.orders.web.mapper.OrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class OrderServiceTest {

    @Value("${topic_name}")
    private String topicName;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private ProductClient productClient;

    @Mock
    private CustomerClient customerClient;

    @Mock
    private KafkaTemplate<String, Serializable> kafkaTemplate;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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
        Customer customer = EntityMock.customer();
        Product product = EntityMock.product();
        Order order = EntityMock.order();
        OrderResponseDto orderResponseDto = EntityMock.responseDto();
        OrderCreateDto orderCreateDto = EntityMock.createDto();

        when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
        when(productClient.getProductBySku(any(String.class))).thenReturn(product);
        when(orderMapper.toOrder(any(Customer.class), any(Product.class))).thenReturn(order);
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(any(Order.class))).thenReturn(orderResponseDto);

        OrderResponseDto response = orderService.save(orderCreateDto);

        verifyResult(response, EntityMock.responseDto());
    }

    @Test
    void testSaveOrderCustomerNotFoundException(){
        when(customerClient.getCustomerByCpf(any(String.class))).thenThrow(HttpClientErrorException.NotFound.class);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            orderService.save(EntityMock.createDto());
        });

        assertEquals("Customer not found by CPF", exception.getMessage());
    }

    @Test
    void testSaveOrderCustomerConnectionExceptionException(){
        when(customerClient.getCustomerByCpf(any(String.class))).thenThrow(HttpServerErrorException.ServiceUnavailable.class);

        ConnectionException exception = assertThrows(ConnectionException.class, () -> {
            orderService.save(EntityMock.createDto());
        });

        assertEquals("Customers API not available", exception.getMessage());
    }

    @Test
    void testSaveOrderCustomerUnknownErrorException(){
        when(customerClient.getCustomerByCpf(any(String.class))).thenThrow(RestClientException.class);

        UnknownErrorException exception = assertThrows(UnknownErrorException.class, () -> {
            orderService.save(EntityMock.createDto());
        });

        assertEquals("Error fetching client by CPF: null", exception.getMessage());
    }

    @Test
    void testSaveOrderProductNotFoundException(){
        when(productClient.getProductBySku(any(String.class))).thenThrow(HttpClientErrorException.NotFound.class);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            orderService.save(EntityMock.createDto());
        });

        assertEquals("Product not found by SKU", exception.getMessage());
    }

    @Test
    void testSaveOrderProductConnectionExceptionException(){
        when(productClient.getProductBySku(any(String.class))).thenThrow(HttpServerErrorException.ServiceUnavailable.class);

        ConnectionException exception = assertThrows(ConnectionException.class, () -> {
            orderService.save(EntityMock.createDto());
        });

        assertEquals("Products API not available", exception.getMessage());
    }

    @Test
    void testSaveOrderProductUnknownErrorException(){
        when(productClient.getProductBySku(any(String.class))).thenThrow(RestClientException.class);

        UnknownErrorException exception = assertThrows(UnknownErrorException.class, () -> {
            orderService.save(EntityMock.createDto());
        });

        assertEquals("Error fetching product by SKU: null", exception.getMessage());
    }

    @Test
    void testSaveOrderInvalidQuantityException() {
        Product product = EntityMock.product();
        OrderCreateDto orderCreateDto = EntityMock.createDto();

        when(productClient.getProductBySku(anyString())).thenReturn(product);
        orderCreateDto.setQuantity(0);

        InvalidQuantityException exception = assertThrows(InvalidQuantityException.class, () -> {
            orderService.save(orderCreateDto);
        });

        assertEquals("No quantity stated", exception.getMessage());
    }

    @Test
    void testSaveOrderUnavailableQuantityException() {
        Product product = EntityMock.product();
        OrderCreateDto orderCreateDto = EntityMock.createDto();
        orderCreateDto.setQuantity(11);

        when(productClient.getProductBySku(anyString())).thenReturn(product);

        InvalidQuantityException exception = assertThrows(InvalidQuantityException.class, () -> {
            orderService.save(orderCreateDto);
        });

        assertEquals("The quantity of available products is " + product.getQuantity(), exception.getMessage());
    }

    @Test
    void testSaveOrderUnavailableProductException() {
        Product product = EntityMock.product();
        product.setStatus(false);
        OrderCreateDto orderCreateDto = EntityMock.createDto();

        when(productClient.getProductBySku(anyString())).thenReturn(product);

        UnavailableProductException exception = assertThrows(UnavailableProductException.class, () -> {
            orderService.save(orderCreateDto);
        });

        assertEquals("The product is not available", exception.getMessage());
    }

    @Test
    void testFindAllSuccess() throws Exception {
        Page<OrderResponseDto> page = EntityMock.page();

        when(orderRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
        when(orderMapper.toPageableDto(any(Page.class))).thenReturn(EntityMock.pageableDto());
        when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(EntityMock.customer());
        when(productClient.getProductBySku(any(String.class))).thenReturn(EntityMock.product());

        PageableDto response = orderService.findAll(null, null, page.getPageable());

        verifyResult(response, EntityMock.responseDto());
    }

    @Test
    void testFindAllWithDateBetweenSuccess() throws Exception {
        Page<OrderResponseDto> page = EntityMock.page();
        String after = LocalDate.now().minusDays(1).toString();
        String before = LocalDate.now().plusDays(1).toString();

        when(orderRepository.findByDateBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(orderMapper.toPageableDto(any(Page.class))).thenReturn(EntityMock.pageableDto());
        when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(EntityMock.customer());
        when(productClient.getProductBySku(any(String.class))).thenReturn(EntityMock.product());

        PageableDto response = orderService.findAll(after, before, page.getPageable());

        verifyResult(response, EntityMock.responseDto());
    }

    @Test
    void testFindAllWithDateAfterSuccess() throws Exception {
        Page<OrderResponseDto> page = EntityMock.page();
        String after = LocalDate.now().minusDays(1).toString();

        when(orderRepository.findByDateAfter(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(orderMapper.toPageableDto(any(Page.class))).thenReturn(EntityMock.pageableDto());
        when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(EntityMock.customer());
        when(productClient.getProductBySku(any(String.class))).thenReturn(EntityMock.product());

        PageableDto response = orderService.findAll(after, null, page.getPageable());

        verifyResult(response, EntityMock.responseDto());
    }

    @Test
    void testFindAllEntityNotFoundException() {
        Page<OrderResponseDto> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(orderRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
        when(orderMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> orderService.findAll(null, null, page.getPageable()));
    }

    @Test
    void testFindBySkuSuccess() {
        Page<OrderResponseDto> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(orderRepository.findAllBySku(any(String.class), any(Pageable.class))).thenReturn(page);
        when(orderMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
        when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(EntityMock.customer());
        when(productClient.getProductBySku(any(String.class))).thenReturn(EntityMock.product());

        PageableDto response = orderService.findBySku(null, null, EntityMock.PRODUCT_SKU, page.getPageable());

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertEquals(1, response.getContent().size());

        OrderResponseDto order = (OrderResponseDto) response.getContent().get(0);
        assertEquals(page.getContent().get(0).getId(), order.getId());
        assertEquals(page.getContent().get(0).getName(), order.getName());
        assertEquals(page.getContent().get(0).getCpf(), order.getCpf());
        assertEquals(page.getContent().get(0).getTitle(), order.getTitle());
        assertEquals(page.getContent().get(0).getSku(), order.getSku());
        assertEquals(page.getContent().get(0).getPrice(), order.getPrice());
        assertEquals(page.getContent().get(0).getQuantity(), order.getQuantity());
        assertEquals(page.getContent().get(0).getTotal(), order.getTotal());
        assertFalse(order.getProcessing());
        assertNotNull(order.getDate());
    }

    @Test
    void testFindBySkuEntityNotFoundException() {
        Page<OrderResponseDto> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(orderRepository.findAllBySku(any(String.class), any(Pageable.class))).thenReturn(page);
        when(orderMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                orderService.findBySku(null, null, EntityMock.PRODUCT_SKU, page.getPageable())
        );

        assertEquals("No orders found", exception.getMessage());
    }

    @Test
    void testFindByCpfSuccess() {
        Page<OrderResponseDto> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(orderRepository.findAllByCpf(any(String.class), any(Pageable.class))).thenReturn(page);
        when(orderMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
        when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(EntityMock.customer());
        when(productClient.getProductBySku(any(String.class))).thenReturn(EntityMock.product());

        PageableDto response = orderService.findByCpf(null, null, EntityMock.CUSTOMER_CPF, page.getPageable());

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertEquals(1, response.getContent().size());

        OrderResponseDto order = (OrderResponseDto) response.getContent().get(0);
        assertEquals(page.getContent().get(0).getId(), order.getId());
        assertEquals(page.getContent().get(0).getName(), order.getName());
        assertEquals(page.getContent().get(0).getCpf(), order.getCpf());
        assertEquals(page.getContent().get(0).getTitle(), order.getTitle());
        assertEquals(page.getContent().get(0).getCpf(), order.getCpf());
        assertEquals(page.getContent().get(0).getPrice(), order.getPrice());
        assertEquals(page.getContent().get(0).getQuantity(), order.getQuantity());
        assertEquals(page.getContent().get(0).getTotal(), order.getTotal());
        assertFalse(order.getProcessing());
        assertNotNull(order.getDate());
    }

    @Test
    void testFindByCpfEntityNotFoundException() {
        Page<OrderResponseDto> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(orderRepository.findAllByCpf(any(String.class), any(Pageable.class))).thenReturn(page);
        when(orderMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                orderService.findByCpf(null, null, EntityMock.CUSTOMER_CPF, page.getPageable())
        );

        assertEquals("No orders found", exception.getMessage());
    }

    @Test
    void testDeleteOrderByIdSuccess() {
        when(orderRepository.existsById(any(Long.class))).thenReturn(true);
        doNothing().when(orderRepository).deleteById(any(Long.class));

        orderService.delete(1L);

        verify(orderRepository).deleteById(1L);
    }

    @Test
    void testDeleteOrderByIdEntityNotFoundException() {
        when(orderRepository.existsById(any(Long.class))).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> orderService.delete(1L));
    }

    @Test
    void testDeleteAllProcessingOrdersSuccess() {
        when(orderRepository.existsByProcessing(eq(true))).thenReturn(true);
        doNothing().when(orderRepository).deleteAllByProcessing(eq(true));

        orderService.deleteAllProcessing();

        verify(orderRepository).deleteAllByProcessing(true);
    }

    @Test
    void testDeleteAllProcessingOrdersEntityNotFoundException() {
        when(orderRepository.existsByProcessing(eq(true))).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> orderService.deleteAllProcessing());
    }
}
