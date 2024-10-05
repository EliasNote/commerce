package com.esand.delivery.repository.delivery;

import com.esand.delivery.entity.Delivery;
import com.esand.delivery.repository.pagination.DeliveryDtoPagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    void deleteAllByStatus(Delivery.Status status);

    boolean existsByStatus(Delivery.Status status);

    Page<DeliveryDtoPagination> findAllByDateAfter(LocalDateTime date, Pageable pageable);

    Page<DeliveryDtoPagination> findAllByDateBefore(LocalDateTime date, Pageable pageable);

    Page<DeliveryDtoPagination> findAllByDateBetween(LocalDateTime afterDate, LocalDateTime beforeDate, Pageable pageable);

    @Query("SELECT p FROM Delivery p")
    Page<DeliveryDtoPagination> findAllPageable(Pageable pageable);

    Page<DeliveryDtoPagination> findAllByStatusAndDateBetween(Delivery.Status status, LocalDateTime localDateTime, LocalDateTime localDateTime1, Pageable pageable);

    Page<DeliveryDtoPagination> findAllByStatusAndDateAfter(Delivery.Status status, LocalDateTime localDateTime, Pageable pageable);

    Page<DeliveryDtoPagination> findAllByStatusAndDateBefore(Delivery.Status status, LocalDateTime localDateTime, Pageable pageable);

    Page<DeliveryDtoPagination> findAllByStatus(Delivery.Status status, Pageable pageable);

    List<Delivery> findAllByStatusAndDateBetween(Delivery.Status status, LocalDateTime localDateTime, LocalDateTime localDateTime1);

    List<Delivery> findAllByStatusAndDateAfter(Delivery.Status status, LocalDateTime localDateTime);

    List<Delivery> findAllByStatusAndDateBefore(Delivery.Status status, LocalDateTime localDateTime);

    List<Delivery> findAllByStatus(Delivery.Status status);

    Page<DeliveryDtoPagination> findAllByCpfAndDateBetween(String cpf, LocalDateTime localDateTime, LocalDateTime localDateTime1, Pageable pageable);

    Page<DeliveryDtoPagination> findAllByCpfAndDateAfter(String cpf, LocalDateTime localDateTime, Pageable pageable);

    Page<DeliveryDtoPagination> findAllByCpfAndDateBefore(String cpf, LocalDateTime localDateTime, Pageable pageable);

    Page<DeliveryDtoPagination> findAllByCpf(String cpf, Pageable pageable);

    Page<DeliveryDtoPagination> findAllBySkuAndDateBetween(String sku, LocalDateTime localDateTime, LocalDateTime localDateTime1, Pageable pageable);

    Page<DeliveryDtoPagination> findAllBySkuAndDateAfter(String sku, LocalDateTime localDateTime, Pageable pageable);

    Page<DeliveryDtoPagination> findAllBySkuAndDateBefore(String sku, LocalDateTime localDateTime, Pageable pageable);

    Page<DeliveryDtoPagination> findAllBySku(String sku, Pageable pageable);
}
