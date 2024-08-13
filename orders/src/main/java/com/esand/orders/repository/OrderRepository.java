package com.esand.orders.repository;

import com.esand.orders.entity.Order;
import com.esand.orders.repository.pagination.OrderDtoPagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT p FROM Order p")
    Page<OrderDtoPagination> findAllPageable(Pageable pageable);

    Optional<Page<OrderDtoPagination>> findBySku(Pageable pageable, String sku);

    Optional<Page<OrderDtoPagination>> findByCpf(Pageable pageable, String cpf);

    void deleteAllByProcessing(Boolean processed);

    Page<OrderDtoPagination> findByDateAfter(LocalDateTime date, Pageable pageable);

    Page<OrderDtoPagination> findByDateBefore(LocalDateTime date, Pageable pageable);

    Page<OrderDtoPagination> findByDateBetween(LocalDateTime afterDate, LocalDateTime beforeDate, Pageable pageable);

    boolean existsByProcessing(boolean b);
}
