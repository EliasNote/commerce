package com.esand.orders.service;

import com.esand.orders.client.clients.Client;
import com.esand.orders.client.clients.ClientClient;
import com.esand.orders.client.products.Product;
import com.esand.orders.client.products.ProductClient;
import com.esand.orders.entity.Order;
import com.esand.orders.repository.OrderRepository;
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

import java.io.Serializable;

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
        Client client;
        Product product;

        try {
            client = clientClient.getClientByName(dto.getClientName());
            product = productClient.getProductByTitle(dto.getProductTitle());
        } catch (RuntimeException e) {
            throw new RuntimeException("Fazer com que seja retornado erro para cada caso usando o e.getMessage(), que deve retornar uma mensagem diferente sendo o erro do client ou do product");
        }

        Order order = orderMapper.toOrder(client, product);
        order.setQuantity(dto.getQuantity());
        order.setTotal(dto.getQuantity() * product.getPrice());

        verifyClientAndProduct(order);

        return orderMapper.toDto(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public PageableDto findAll(Pageable pageable) {
        return orderMapper.toPageableDto(orderRepository.findAllPageable(pageable));
    }

    @Transactional(readOnly = true)
    public PageableDto findBySku(Pageable pageable, String sku) {
        return orderMapper.toPageableDto(orderRepository.findBySku(pageable, sku).orElseThrow());
    }

    @Transactional(readOnly = true)
    public PageableDto findByCpf(Pageable pageable, String cpf) {
        return orderMapper.toPageableDto(orderRepository.findByCpf(pageable, cpf).orElseThrow());
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
                () -> new RuntimeException("Order nº" + id + " does not exist")
        );
        if (order.getProcessed()) {
            throw new RuntimeException("Already processed order");
        }

        verifyClientAndProduct(order);

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

    private void verifyClientAndProduct(Order order) {
        Client client;
        Product product;

        try {
            client = clientClient.getClientByName(order.getName());
            product = productClient.getProductByTitle(order.getTitle());
        } catch (RuntimeException e) {
            throw new RuntimeException("Fazer com que seja retornado erro para cada caso usando o e.getMessage(), que deve retornar uma mensagem diferente sendo o erro do client ou do product");
        }

        if (!client.getStatus()) {
            throw new RuntimeException("Client deactivated");
        }

        if (!product.getStatus()) {
            throw new RuntimeException("The product is not available");
        }
        if (order.getQuantity() == null || order.getQuantity() == 0) {
            throw new RuntimeException("No quantity informed");
        }
        if (order.getQuantity() > product.getQuantity()) {
            throw new RuntimeException("The quantity of available products is " + product.getQuantity());
        }
    }


}