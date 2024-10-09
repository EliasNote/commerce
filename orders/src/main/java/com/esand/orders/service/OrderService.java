package com.esand.orders.service;

import com.esand.orders.client.customers.Customer;
import com.esand.orders.client.customers.CustomerClient;
import com.esand.orders.client.products.Product;
import com.esand.orders.client.products.ProductClient;
import com.esand.orders.entity.Order;
import com.esand.orders.exception.*;
import com.esand.orders.repository.order.OrderRepository;
import com.esand.orders.repository.pagination.OrderDtoPagination;
import com.esand.orders.web.dto.OrderCreateDto;
import com.esand.orders.web.dto.OrderResponseDto;
import com.esand.orders.web.dto.PageableDto;
import com.esand.orders.web.mapper.OrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    @Value("${topic_name}")
    private String topicName;

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductClient productClient;
    private final CustomerClient customerClient;
    private final KafkaTemplate<String, Serializable> kafkaTemplate;

    @Transactional
    public OrderResponseDto save(OrderCreateDto dto) {
        verifyIfExistsClientAndProductAndConnection(dto.getCpf(), dto.getSku());
        verifyProduct(dto.getSku(), dto.getQuantity());

        Customer customer = customerClient.getCustomerByCpf(dto.getCpf());
        Product product = productClient.getProductBySku(dto.getSku());

        Order order = orderMapper.toOrder(customer, product);
        order.setQuantity(dto.getQuantity());
        order.setTotal(dto.getQuantity() * product.getPrice());

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional
    public PageableDto findAll(String afterDate, String beforeDate, Pageable pageable) {
        return findByCriteria(null, null, afterDate, beforeDate, pageable);
    }

    @Transactional
    public PageableDto findBySku(Pageable pageable, String sku) {
        return findByCriteria(null, sku, null, null, pageable);
    }

    @Transactional
    public PageableDto findByCpf(Pageable pageable, String cpf) {
        return findByCriteria(cpf, null, null, null, pageable);
    }

    @Transactional
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new EntityNotFoundException("Order not found");
        }
        orderRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllProcessing() {
        if (!orderRepository.existsByProcessing(true)) {
            throw new EntityNotFoundException("No orders processing found");
        }
        orderRepository.deleteAllByProcessing(true);
    }

    public String sendOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Order nº" + id + " does not exist")
        );
        if (order.getProcessing()) {
            throw new OrderAlreadySentException("Already processing order");
        }

        verifyIfExistsClientAndProductAndConnection(order.getCpf(), order.getSku());
        verifyProduct(order.getSku(), order.getQuantity());

        order.setProcessing(true);
        productClient.decreaseProductQuantityBySku(order.getSku(), order.getQuantity());
        OrderResponseDto response = orderMapper.toDto(orderRepository.save(order));
        sendMessage(response);
        return "Order nº" + id + " is processing successfully";
    }

    private void sendMessage(OrderResponseDto dto) {
        kafkaTemplate.send(topicName, dto).whenComplete((result, e) -> {
            if (e == null) {
                log.info("Order successfully accepted: nº{}", dto.getId());
                log.info("Partition: {}", result.getRecordMetadata().partition());
                log.info("Offset: {}", result.getRecordMetadata().offset());
            } else {
                log.error("Error in order submission", e);
            }
        });
    }

    private void verifyIfExistsClientAndProductAndConnection(String cpf, String sku) {
        try {
            customerClient.getCustomerByCpf(cpf);
        } catch (HttpClientErrorException.NotFound e) {
            throw new EntityNotFoundException("Customer not found by CPF");
        } catch (HttpServerErrorException.ServiceUnavailable e) {
            throw new ConnectionException("Customers API not available");
        } catch (RestClientException e) {
            throw new UnknownErrorException("Error fetching client by CPF: " + e.getMessage());
        }

        try {
            productClient.getProductBySku(sku);
        } catch (HttpClientErrorException.NotFound e) {
            throw new EntityNotFoundException("Product not found by SKU");
        } catch (HttpServerErrorException.ServiceUnavailable e) {
            throw new ConnectionException("Products API not available");
        } catch (RestClientException e) {
            throw new UnknownErrorException("Error fetching product by SKU: " + e.getMessage());
        }
    }

    private void verifyProduct(String sku, Integer quantity) {
        Product product = productClient.getProductBySku(sku);
        if (quantity == null || quantity == 0) {
            throw new InvalidQuantityException("No quantity stated");
        }
        if (quantity > product.getQuantity()) {
            throw new InvalidQuantityException("The quantity of available products is " + product.getQuantity());
        }
        if (!product.getStatus()) {
            throw new UnavailableProductException("The product is not available");
        }
    }

    @Transactional
    private void updateNameAndTitle(String sku, String cpf) {
        List<Order> orders;

        if (sku != null || cpf != null) {
            if (sku != null) {
                orders = orderRepository.findBySku(sku);
            } else {
                orders = orderRepository.findByCpf(cpf);
            }
        } else {
            orders = orderRepository.findAll();
        }

        if (orders.isEmpty()) {
            throw new EntityNotFoundException("No orders found");
        }

        for (Order order : orders) {
            order.setName(customerClient.getCustomerByCpf(order.getCpf()).getName());
            order.setTitle(productClient.getProductBySku(order.getSku()).getTitle());
            orderRepository.save(order);
        }
    }

    @Transactional
    private PageableDto findByCriteria(String cpf, String sku, String afterDate, String beforeDate, Pageable pageable) {
        LocalDateTime after = null;
        LocalDateTime before = null;
        PageableDto dto;

        if (afterDate != null) {
            after = LocalDate.parse(afterDate).atStartOfDay();
        }
        if (beforeDate != null) {
            before = LocalDate.parse(beforeDate).plusDays(1).atStartOfDay();
        }

        if (cpf != null) {
            updateNameAndTitle(null, cpf);
            dto = orderMapper.toPageableDto(orderRepository.findByCpf(pageable, cpf));
        } else if (sku != null) {
            updateNameAndTitle(sku, null);
            dto = orderMapper.toPageableDto(orderRepository.findBySku(pageable, sku));
        } else {
            updateNameAndTitle(null, null);
            if (after != null && before != null) {
                dto = orderMapper.toPageableDto(orderRepository.findByDateBetween(after, before, pageable));
            } else if (after != null) {
                dto = orderMapper.toPageableDto(orderRepository.findByDateAfter(after, pageable));
            } else if (before != null) {
                dto = orderMapper.toPageableDto(orderRepository.findByDateBefore(before, pageable));
            } else {
                dto = orderMapper.toPageableDto(orderRepository.findAllPageable(pageable));
            }
        }

        return dto;
    }
}