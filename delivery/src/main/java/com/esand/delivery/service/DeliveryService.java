package com.esand.delivery.service;

import com.esand.delivery.client.products.ProductClient;
import com.esand.delivery.entity.Delivery;
import com.esand.delivery.exception.ConnectionException;
import com.esand.delivery.exception.EntityNotFoundException;
import com.esand.delivery.exception.OrderCanceledException;
import com.esand.delivery.exception.OrderShippedException;
import com.esand.delivery.repository.DeliveryRepository;
import com.esand.delivery.web.dto.DeliveryResponseDto;
import com.esand.delivery.web.dto.DeliverySaveDto;
import com.esand.delivery.web.dto.PageableDto;
import com.esand.delivery.web.mapper.DeliveryMapper;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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
    public PageableDto findAll(Pageable pageable) {
        PageableDto dto = deliveryMapper.toPageableDto(deliveryRepository.findAllPageable(pageable));
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
    public PageableDto findAllShipped(Pageable pageable) {
        PageableDto dto =  deliveryMapper.toPageableDto(deliveryRepository.findAllByStatus(pageable, Delivery.Status.SHIPPED));
        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No orders found");
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public PageableDto findAllProcessing(Pageable pageable) {
        PageableDto dto =  deliveryMapper.toPageableDto(deliveryRepository.findAllByStatus(pageable, Delivery.Status.PROCESSING));
        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No orders found");
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public PageableDto findAllCanceled(Pageable pageable) {
        PageableDto dto =  deliveryMapper.toPageableDto(deliveryRepository.findAllByStatus(pageable, Delivery.Status.CANCELED));
        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No orders found");
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public PageableDto findDeliveryByDate(String afterDate, String beforeDate, Pageable pageable) {
        PageableDto dto;
        if (afterDate != null && beforeDate != null) {
            dto = deliveryMapper.toPageableDto(deliveryRepository.findByDateBetween(LocalDate.parse(afterDate).atStartOfDay(), LocalDate.parse(beforeDate).atStartOfDay().plusDays(1), pageable));
        } else if (afterDate != null) {
            dto = deliveryMapper.toPageableDto(deliveryRepository.findByDateAfter(LocalDate.parse(afterDate).atStartOfDay(), pageable));
        } else if (beforeDate != null) {
            dto = deliveryMapper.toPageableDto(deliveryRepository.findByDateBefore(LocalDate.parse(beforeDate).atStartOfDay().plusDays(1), pageable));
        } else {
            throw new EntityNotFoundException("No date parameters provided");
        }

        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No deliveries found by date(s)");
        }

        return dto;
    }

    @Transactional(noRollbackFor= Exception.class)
    public String cancel(Long id) {
        Delivery delivery = findOrderById(id);
        if (delivery.getStatus().equals(Delivery.Status.CANCELED)) {
            throw new OrderCanceledException("Order nº" + delivery.getId() + " has already been canceled");
        }

        try {
            productClient.checkStatus();
        } catch (FeignException.ServiceUnavailable e) {
            throw new ConnectionException(e.getMessage());
        }

        delivery.setStatus(Delivery.Status.CANCELED);

        try {
            productClient.addProductQuantityBySku(delivery.getSku(), delivery.getQuantity());
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException(e.getMessage());
        }

        return "Order nº" + delivery.getId() + " status changed to canceled successfully";
    }

    @Transactional
    public String statusShipped(Long id) {
        Delivery delivery = findOrderById(id);
        if (delivery.getStatus().equals(Delivery.Status.SHIPPED)) {
            throw new OrderShippedException("Order nº" + delivery.getId() + " has already been shipped");
        }
        if (delivery.getStatus().equals(Delivery.Status.CANCELED)) {
            throw new OrderCanceledException("Order nº" + delivery.getId() + " is canceled");
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

    private Delivery findOrderById(Long id) {
        return deliveryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Order nº" + id + " does not exist")
        );
    }
}
