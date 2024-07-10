package com.esand.delivery.service;

import com.esand.delivery.client.products.ProductClient;
import com.esand.delivery.entity.Delivery;
import com.esand.delivery.repository.DeliveryRepository;
import com.esand.delivery.web.dto.DeliveryResponseDto;
import com.esand.delivery.web.dto.DeliverySaveDto;
import com.esand.delivery.web.mapper.DeliveryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<DeliveryResponseDto> findAll() {
        return deliveryRepository.findAll().stream().map(x -> deliveryMapper.toDto(x)).toList();
    }

    @Transactional(readOnly = true)
    public DeliveryResponseDto findById(Long id) {
        Delivery delivery = deliveryRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Order nº" + id + " does not exist")
        );
        return deliveryMapper.toDto(delivery);
    }

    @Transactional
    public String cancel(Long id) {
        Delivery delivery = deliveryRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Order nº" + id + " does not exist")
        );
        delivery.setStatus(Delivery.Status.CANCELED);
        productClient.addProductQuantityBySku(delivery.getSku(), delivery.getQuantity());
        return "Order nº" + delivery.getId() + " status changed to canceled successfully";
    }

    @Transactional
    public String statusShipped(Long id) {
        Delivery delivery = deliveryRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Order nº" + id + " does not exist")
        );
        delivery.setStatus(Delivery.Status.SHIPPED);
        return "Order nº" + delivery.getId() + " status changed to shipped successfully";
    }

    @Transactional
    public void deleteAllCanceled() {
        deliveryRepository.deleteAllByStatus(Delivery.Status.CANCELED);
    }
}
