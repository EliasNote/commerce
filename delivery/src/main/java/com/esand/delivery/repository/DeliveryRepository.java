package com.esand.delivery.repository;

import com.esand.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    void deleteAllByStatus(Delivery.Status status);
}
