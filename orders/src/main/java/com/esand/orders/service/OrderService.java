package com.esand.orders.service;

import com.esand.orders.client.clients.Client;
import com.esand.orders.client.clients.ClientClient;
import com.esand.orders.client.products.Product;
import com.esand.orders.client.products.ProductClient;
import com.esand.orders.entity.Order;
import com.esand.orders.exception.*;
import com.esand.orders.repository.OrderRepository;
import com.esand.orders.web.dto.OrderCreateDto;
import com.esand.orders.web.dto.OrderResponseDto;
import com.esand.orders.web.dto.PageableDto;
import com.esand.orders.web.mapper.OrderMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    @Value("${topic_name}")
    private String topicName;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductClient productClient;
    private final ClientClient clientClient;
    private final KafkaTemplate<String, Serializable> kafkaTemplate;

    @Transactional
    public OrderResponseDto save(OrderCreateDto dto) {
        verifyIfExistsClientAndProductAndConnection(dto.getCpf(), dto.getSku());
        verifyProduct(dto.getSku(), dto.getQuantity());

        Client client = clientClient.getClientByCpf(dto.getCpf());
        Product product = productClient.getProductBySku(dto.getSku());

        Order order = orderMapper.toOrder(client, product);
        order.setQuantity(dto.getQuantity());
        order.setTotal(dto.getQuantity() * product.getPrice());

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public PageableDto findAll(Pageable pageable) {
        PageableDto dto = orderMapper.toPageableDto(orderRepository.findAllPageable(pageable));
        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No orders found");
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public PageableDto findBySku(Pageable pageable, String sku) {
        PageableDto dto = orderMapper.toPageableDto(orderRepository.findBySku(pageable, sku).orElseThrow());
        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No orders found by sku");
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public PageableDto findByCpf(Pageable pageable, String cpf) {
        PageableDto dto = orderMapper.toPageableDto(orderRepository.findByCpf(pageable, cpf).orElseThrow());
        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No orders found by cpf");
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public PageableDto findOrdersByDate(String afterDate, String beforeDate, Pageable pageable) {
        PageableDto dto;
        if (afterDate != null && beforeDate != null) {
            dto = orderMapper.toPageableDto(orderRepository.findByDateBetween(LocalDate.parse(afterDate).atStartOfDay(), LocalDate.parse(beforeDate).atStartOfDay().plusDays(1), pageable));
        } else if (afterDate != null) {
            dto = orderMapper.toPageableDto(orderRepository.findByDateAfter(LocalDate.parse(afterDate).atStartOfDay(), pageable));
        } else if (beforeDate != null) {
            dto = orderMapper.toPageableDto(orderRepository.findByDateBefore(LocalDate.parse(beforeDate).atStartOfDay().plusDays(1), pageable));
        } else {
            throw new EntityNotFoundException("No date parameters provided");
        }

        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No orders found by date(s)");
        }

        return dto;
    }

    @Transactional
    public void delete(Long id) {
        orderRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllProcessed() {
        orderRepository.deleteAllByProcessed(true);
    }

    public String sendOrder(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Order nº" + id + " does not exist")
        );
        if (order.getProcessed()) {
            throw new OrderAlreadySentException("Already processed order");
        }

        verifyIfExistsClientAndProductAndConnection(order.getCpf(), order.getSku());
        verifyProduct(order.getSku(), order.getQuantity());

        order.setProcessed(true);
        productClient.decreaseProductQuantityBySku(order.getSku(), order.getQuantity());
        OrderResponseDto response = orderMapper.toDto(orderRepository.save(order));
        sendMessage(response);
        return "Order nº" + id + " processed successfully";
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
            clientClient.getClientByCpf(cpf);
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException(e.getMessage());
        } catch (FeignException.ServiceUnavailable e) {
            throw new ConnectionException(e.getMessage());
        }

        try {
            productClient.getProductBySku(sku);
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException(e.getMessage());
        } catch (FeignException.ServiceUnavailable e) {
            throw new ConnectionException(e.getMessage());
        }
    }

    private void verifyProduct(String sku, Integer quantity) {
        Product product = productClient.getProductBySku(sku);
        if (quantity == null || quantity == 0) {
            throw new InvalidQuantityException("No quantity informed");
        }
        if (quantity > product.getQuantity()) {
            throw new InvalidQuantityException("The quantity of available products is " + product.getQuantity());
        }
        if (!product.getStatus()) {
            throw new UnavailableProductException("The product is not available");
        }
    }
}