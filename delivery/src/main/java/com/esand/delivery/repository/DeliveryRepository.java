package com.esand.delivery.repository;

import com.esand.delivery.entity.Delivery;
import com.esand.delivery.repository.pagination.DeliveryDtoPagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    void deleteAllByStatus(Delivery.Status status);

    @Query("SELECT p FROM Delivery p")
    Page<DeliveryDtoPagination> findAllPageable(Pageable pageable);

    Page<DeliveryDtoPagination> findAllByStatus(Pageable pageable, Delivery.Status status);

    Page<DeliveryDtoPagination> findByDateAfter(LocalDateTime date, Pageable pageable);

    Page<DeliveryDtoPagination> findByDateBefore(LocalDateTime date, Pageable pageable);

    Page<DeliveryDtoPagination> findByDateBetween(LocalDateTime afterDate, LocalDateTime beforeDate, Pageable pageable);
}
