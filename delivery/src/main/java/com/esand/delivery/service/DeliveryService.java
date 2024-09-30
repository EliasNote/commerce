package com.esand.delivery.service;

import com.esand.delivery.client.products.ProductClient;
import com.esand.delivery.entity.Delivery;
import com.esand.delivery.exception.ConnectionException;
import com.esand.delivery.exception.EntityNotFoundException;
import com.esand.delivery.exception.DeliveryCanceledException;
import com.esand.delivery.exception.DeliveryShippedException;
import com.esand.delivery.repository.DeliveryRepository;
import com.esand.delivery.web.dto.DeliveryResponseDto;
import com.esand.delivery.web.dto.DeliverySaveDto;
import com.esand.delivery.web.dto.PageableDto;
import com.esand.delivery.web.mapper.DeliveryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final ProductClient productClient;

    @Transactional
    public void save(DeliverySaveDto dto) {
        deliveryRepository.save(deliveryMapper.toDelivery(dto));
    }

    @Transactional(readOnly = true)
    public PageableDto findAll(String afterDate, String beforeDate, Pageable pageable) {
        PageableDto dto;

        if (afterDate != null && beforeDate != null) {
            dto = deliveryMapper.toPageableDto(deliveryRepository.findAllByDateBetween(LocalDate.parse(afterDate).atStartOfDay(), LocalDate.parse(beforeDate).atStartOfDay().plusDays(1), pageable));
        } else if (afterDate != null) {
            dto = deliveryMapper.toPageableDto(deliveryRepository.findAllByDateAfter(LocalDate.parse(afterDate).atStartOfDay(), pageable));
        } else if (beforeDate != null) {
            dto = deliveryMapper.toPageableDto(deliveryRepository.findAllByDateBefore(LocalDate.parse(beforeDate).atStartOfDay().plusDays(1), pageable));
        } else {
            dto = deliveryMapper.toPageableDto(deliveryRepository.findAllPageable(pageable));
        }

        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No orders found");
        }

        return dto;
    }

    @Transactional(readOnly = true)
    public DeliveryResponseDto findById(Long id) {
        return deliveryMapper.toDto(findOrderById(id));
    }

    @Transactional(readOnly = true)
    public PageableDto findAllShipped(String afterDate, String beforeDate, Pageable pageable) {
        return findByStatusAndDate(afterDate, beforeDate, Delivery.Status.SHIPPED, pageable);
    }

    @Transactional(readOnly = true)
    public PageableDto findAllProcessing(String afterDate, String beforeDate, Pageable pageable) {
        return findByStatusAndDate(afterDate, beforeDate, Delivery.Status.PROCESSING, pageable);
    }

    @Transactional(readOnly = true)
    public PageableDto findAllCanceled(String afterDate, String beforeDate, Pageable pageable) {
        return findByStatusAndDate(afterDate, beforeDate, Delivery.Status.CANCELED, pageable);
    }

    @Transactional(noRollbackFor= Exception.class)
    public String cancel(Long id) {
        Delivery delivery = findOrderById(id);
        if (delivery.getStatus().equals(Delivery.Status.CANCELED)) {
            throw new DeliveryCanceledException("Order nº" + delivery.getId() + " has already been canceled");
        }

        try {
            productClient.checkStatus();
        } catch (HttpServerErrorException.ServiceUnavailable e) {
            throw new ConnectionException("Customers API not available");
        }

        delivery.setStatus(Delivery.Status.CANCELED);

        try {
            productClient.addProductQuantityBySku(delivery.getSku(), delivery.getQuantity());
        } catch (HttpClientErrorException.NotFound e) {
            throw new EntityNotFoundException("Customer not found by CPF");
        }

        return "Order nº" + delivery.getId() + " status changed to canceled successfully";
    }

    @Transactional
    public String statusShipped(Long id) {
        Delivery delivery = findOrderById(id);
        if (delivery.getStatus().equals(Delivery.Status.SHIPPED)) {
            throw new DeliveryShippedException("Order nº" + delivery.getId() + " has already been shipped");
        }
        if (delivery.getStatus().equals(Delivery.Status.CANCELED)) {
            throw new DeliveryCanceledException("Order nº" + delivery.getId() + " is canceled");
        }
        delivery.setStatus(Delivery.Status.SHIPPED);
        return "Order nº" + delivery.getId() + " status changed to shipped successfully";
    }

    @Transactional
    public void deleteAllCanceled() {
        if (!deliveryRepository.existsByStatus(Delivery.Status.CANCELED)) {
            throw new EntityNotFoundException("No deliveries canceled found");
        }
        deliveryRepository.deleteAllByStatus(Delivery.Status.CANCELED);
    }

    @Transactional(readOnly = true)
    private Delivery findOrderById(Long id) {
        return deliveryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Order nº" + id + " does not exist")
        );
    }

    @Transactional(readOnly = true)
    private PageableDto findByStatusAndDate(String afterDate, String beforeDate, Delivery.Status status, Pageable pageable) {
        PageableDto dto = null;
        
        if (afterDate != null && beforeDate != null) {
            dto = deliveryMapper.toPageableDto(deliveryRepository.findAllByStatusAndDateBetween(status, LocalDate.parse(afterDate).atStartOfDay(), LocalDate.parse(beforeDate).atStartOfDay().plusDays(1), pageable));
        } else if (afterDate != null) {
            dto = deliveryMapper.toPageableDto(deliveryRepository.findAllByStatusAndDateAfter(status, LocalDate.parse(afterDate).atStartOfDay(), pageable));
        } else if (beforeDate != null) {
            dto = deliveryMapper.toPageableDto(deliveryRepository.findAllByStatusAndDateBefore(status, LocalDate.parse(beforeDate).atStartOfDay().plusDays(1), pageable));
        } else {
            dto = deliveryMapper.toPageableDto(deliveryRepository.findAllByStatus(pageable, status));
        }

        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No orders found");
        }
        
        return dto;
    }

    @Transactional(readOnly = true)
    private List<Delivery> findAllByDate(String afterDate, String beforeDate) {
        List<Delivery> deliveries = null;

        if (afterDate != null && beforeDate != null) {
            deliveries = deliveryRepository.findAllByStatusAndDateBetween(Delivery.Status.SHIPPED, LocalDate.parse(afterDate).atStartOfDay(), LocalDate.parse(beforeDate).atStartOfDay().plusDays(1));
        } else if (afterDate != null) {
            deliveries = deliveryRepository.findAllByStatusAndDateAfter(Delivery.Status.SHIPPED, LocalDate.parse(afterDate).atStartOfDay());
        } else if (beforeDate != null) {
            deliveries = deliveryRepository.findAllByStatusAndDateBefore(Delivery.Status.SHIPPED, LocalDate.parse(beforeDate).atStartOfDay().plusDays(1));
        } else {
            deliveries = deliveryRepository.findAllByStatus(Delivery.Status.SHIPPED);
        }

        return deliveries;
    }

    public String findTopShippedByCustomers(String afterDate, String beforeDate) {
        StringBuilder sb = new StringBuilder();

        List<Delivery> shippeds = findAllByDate(afterDate, beforeDate);

        Map<String, Map<String, Object>> customerData = shippeds.stream()
                .collect(Collectors.groupingBy(Delivery::getCpf,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                deliveries -> {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("customerName", deliveries.stream().findFirst().map(Delivery::getName).orElse("Name not found"));
                                    data.put("totalQuantity", deliveries.stream().mapToLong(Delivery::getQuantity).sum());
                                    data.put("totalSpent", deliveries.stream().mapToDouble(Delivery::getTotal).sum());
                                    return data;
                                }
                        )
                ));

        List<Map.Entry<String, Map<String, Object>>> sortedCustomers = customerData.entrySet().stream()
                .sorted((e1, e2) -> Double.compare((Double) e2.getValue().get("totalSpent"), (Double) e1.getValue().get("totalSpent")))
                .toList();

        int count = 1;
        for (Map.Entry<String, Map<String, Object>> entry : sortedCustomers) {
            sb.append(count++)
                    .append(" - Customer: " + entry.getValue().get("customerName"))
                    .append(", CPF: " + entry.getKey())
                    .append(", Products Purchased: " + entry.getValue().get("totalQuantity"))
                    .append(", Total Spent: $" + entry.getValue().get("totalSpent"))
                    .append("\n");
        }

        return sb.toString();
    }



    public String findTopShippedByProducts(String afterDate, String beforeDate) {
        StringBuilder sb = new StringBuilder();

        List<Delivery> shippeds = findAllByDate(afterDate, beforeDate);

        Map<String, Map<String, Object>> productData = shippeds.stream()
                .collect(Collectors.groupingBy(Delivery::getSku,
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                deliveries -> {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("productTitle", deliveries.stream().findFirst().map(Delivery::getTitle).orElse("Product not found"));
                                    data.put("totalQuantity", deliveries.stream().mapToLong(Delivery::getQuantity).sum());
                                    data.put("totalRevenue", deliveries.stream().mapToDouble(Delivery::getTotal).sum());
                                    return data;
                                }
                        )
                ));

        List<Map.Entry<String, Map<String, Object>>> sortedProducts = productData.entrySet().stream()
                .sorted((e1, e2) -> Double.compare((Double) e2.getValue().get("totalRevenue"), (Double) e1.getValue().get("totalRevenue")))
                .toList();

        int count = 1;
        for (Map.Entry<String, Map<String, Object>> entry : sortedProducts) {
            sb.append(count++)
                    .append(" - Product: " + entry.getValue().get("productTitle"))
                    .append(", Sku: " + entry.getKey())
                    .append(", Total Sold: " + entry.getValue().get("totalQuantity"))
                    .append(", Total Revenue: $" + entry.getValue().get("totalRevenue"))
                    .append("\n");
        }

        return sb.toString();
    }

}
