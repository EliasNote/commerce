package com.esand.delivery.service;

import com.esand.delivery.client.customers.CustomerClient;
import com.esand.delivery.client.products.ProductClient;
import com.esand.delivery.entity.Delivery;
import com.esand.delivery.exception.*;
import com.esand.delivery.repository.delivery.DeliveryRepository;
import com.esand.delivery.web.dto.DeliveryResponseDto;
import com.esand.delivery.web.dto.DeliverySaveDto;
import com.esand.delivery.web.dto.PageableDto;
import com.esand.delivery.web.mapper.DeliveryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cglib.core.Local;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryMapper deliveryMapper;
    private final ProductClient productClient;
    private final CustomerClient customerClient;

    @Transactional
    public void save(DeliverySaveDto dto) {
        deliveryRepository.save(deliveryMapper.toDelivery(dto));
    }

    @Transactional
    public PageableDto findAll(String afterDate, String beforeDate, Pageable pageable) {
        return findByCriteria(null, null, null, null, afterDate, beforeDate, pageable);
    }

    @Transactional
    public DeliveryResponseDto findById(Long id) {
        return deliveryMapper.toDto(findOrderById(id));
    }

    @Transactional
    public PageableDto findAllShipped(String afterDate, String beforeDate, Pageable pageable) {
        return findByCriteria(null, null, null, Delivery.Status.SHIPPED, afterDate, beforeDate, pageable);
    }

    @Transactional
    public PageableDto findAllProcessing(String afterDate, String beforeDate, Pageable pageable) {
        return findByCriteria(null, null, null, Delivery.Status.PROCESSING, afterDate, beforeDate, pageable);
    }

    @Transactional
    public PageableDto findAllCanceled(String afterDate, String beforeDate, Pageable pageable) {
        return findByCriteria(null, null, null, Delivery.Status.CANCELED, afterDate, beforeDate, pageable);
    }

    @Transactional
    public PageableDto findAllByCpf(String cpf, String afterDate, String beforeDate, Pageable pageable) {
        return findByCriteria(cpf, null, null, null, afterDate, beforeDate, pageable);
    }

    @Transactional
    public PageableDto findAllBySku(String sku, String afterDate, String beforeDate, Pageable pageable) {
        return findByCriteria(null, sku, null, null, afterDate, beforeDate, pageable);
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

    @Transactional
    public void deleteById(Long id) {
        if (!deliveryRepository.existsById(id)) {
            throw new EntityNotFoundException("Delivery not found");
        }
        deliveryRepository.deleteById(id);
    }

    @Transactional
    private Delivery findOrderById(Long id) {
        return deliveryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Order nº" + id + " does not exist")
        );
    }

    @Transactional
    private PageableDto findByCriteria(String cpf, String sku, Long id,  Delivery.Status status, String afterDate, String beforeDate, Pageable pageable) {
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
            updateNameAndTitle(cpf, sku, id, status);
            if (after != null && before != null) {
                dto = deliveryMapper.toPageableDto(deliveryRepository.findAllByCpfAndDateBetween(cpf, after, before, pageable));
            } else if (after != null) {
                dto = deliveryMapper.toPageableDto(deliveryRepository.findAllByCpfAndDateAfter(cpf, after, pageable));
            } else if (before != null) {
                dto = deliveryMapper.toPageableDto(deliveryRepository.findAllByCpfAndDateBefore(cpf, before, pageable));
            } else {
                dto = deliveryMapper.toPageableDto(deliveryRepository.findAllByCpf(cpf, pageable));
            }
        } else if (sku != null) {
            updateNameAndTitle(cpf, sku, id, status);
            if (after != null && before != null) {
                dto = deliveryMapper.toPageableDto(deliveryRepository.findAllBySkuAndDateBetween(sku, after, before, pageable));
            } else if (after != null) {
                dto = deliveryMapper.toPageableDto(deliveryRepository.findAllBySkuAndDateAfter(sku, after, pageable));
            } else if (before != null) {
                dto = deliveryMapper.toPageableDto(deliveryRepository.findAllBySkuAndDateBefore(sku, before, pageable));
            } else {
                dto = deliveryMapper.toPageableDto(deliveryRepository.findAllBySku(sku, pageable));
            }
        } else if (status != null) {
            updateNameAndTitle(cpf, sku, id, status);
            if (after != null && before != null) {
                dto = deliveryMapper.toPageableDto(deliveryRepository.findAllByStatusAndDateBetween(status, after, before, pageable));
            } else if (after != null) {
                dto = deliveryMapper.toPageableDto(deliveryRepository.findAllByStatusAndDateAfter(status, after, pageable));
            } else if (before != null) {
                dto = deliveryMapper.toPageableDto(deliveryRepository.findAllByStatusAndDateBefore(status, before, pageable));
            } else {
                dto = deliveryMapper.toPageableDto(deliveryRepository.findAllByStatus(status, pageable));
            }
        } else {
            updateNameAndTitle(cpf, sku, id, status);
            if (after != null && before != null) {
                dto = deliveryMapper.toPageableDto(deliveryRepository.findAllByDateBetween(after, before, pageable));
            } else if (after != null) {
                dto = deliveryMapper.toPageableDto(deliveryRepository.findAllByDateAfter(after, pageable));
            } else if (before != null) {
                dto = deliveryMapper.toPageableDto(deliveryRepository.findAllByDateBefore(before, pageable));
            } else {
                dto = deliveryMapper.toPageableDto(deliveryRepository.findAllPageable(pageable));
            }
        }

        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No orders found");
        }

        return dto;
    }

    @Transactional
    private void updateNameAndTitle(String cpf, String sku, Long id, Delivery.Status status) {
        if (id != null) {
            Delivery delivery = findOrderById(id);
            delivery.setName(customerClient.getCustomerByCpf(delivery.getCpf()).getName());
            delivery.setTitle(productClient.getProductBySku(delivery.getSku()).getTitle());
        } else {
            List<Delivery> deliveries;

            if (cpf != null) {
                deliveries = deliveryRepository.findAllByCpf(cpf);
            } else if (sku != null) {
                deliveries = deliveryRepository.findAllBySku(sku);
            } else if (status != null) {
                deliveries = deliveryRepository.findAllByStatus(status);
            } else {
                deliveries = deliveryRepository.findAll();
            }

            for (Delivery delivery : deliveries) {
                try {
                    delivery.setName(customerClient.getCustomerByCpf(delivery.getCpf()).getName());
                    delivery.setTitle(productClient.getProductBySku(delivery.getSku()).getTitle());
                    deliveryRepository.save(delivery);
                } catch (HttpClientErrorException.NotFound e) {
                    System.out.println("Customer or product not found for delivery with ID: " + delivery.getId());
                } catch (RestClientException e) {
                    System.out.println("Error fetching customer or product: " + e.getMessage());
                }
            }
        }
    }

    @Transactional
    private List<Delivery> findAllByDate(String afterDate, String beforeDate) {
        List<Delivery> deliveries;

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

        updateNameAndTitle(null, null, null, null);

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

        updateNameAndTitle(null, null, null, null);

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
