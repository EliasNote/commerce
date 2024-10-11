package com.esand.orders.repository.order;

import com.esand.orders.entity.Order;
import com.esand.orders.web.dto.OrderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface OrderRepository extends JpaRepository<Order, Long> {

    void deleteAllByProcessing(Boolean processed);

    boolean existsByProcessing(boolean b);

    @Query("SELECT new com.esand.orders.web.dto.OrderResponseDto(p.id, null, p.cpf, null, p.sku, p.price, p.quantity, p.total, p.processing, p.date) FROM Order p")
    Page<OrderResponseDto> findAllPageable(Pageable pageable);

    @Query("SELECT new com.esand.orders.web.dto.OrderResponseDto(p.id, null, p.cpf, null, p.sku, p.price, p.quantity, p.total, p.processing, p.date) FROM Order p WHERE p.sku = :sku AND p.date > :date")
    Page<OrderResponseDto> findAllBySkuAndDateAfter(@Param("sku") String sku, @Param("date") LocalDateTime date, Pageable pageable);

    @Query("SELECT new com.esand.orders.web.dto.OrderResponseDto(p.id, null, p.cpf, null, p.sku, p.price, p.quantity, p.total, p.processing, p.date) FROM Order p WHERE p.sku = :sku AND p.date < :date")
    Page<OrderResponseDto> findAllBySkuAndDateBefore(@Param("sku") String sku, @Param("date") LocalDateTime date, Pageable pageable);

    @Query("SELECT new com.esand.orders.web.dto.OrderResponseDto(p.id, null, p.cpf, null, p.sku, p.price, p.quantity, p.total, p.processing, p.date) FROM Order p WHERE p.sku = :sku AND p.date BETWEEN :afterDate AND :beforeDate")
    Page<OrderResponseDto> findAllBySkuAndDateBetween(@Param("sku") String sku, @Param("afterDate") LocalDateTime afterDate, @Param("beforeDate") LocalDateTime beforeDate, Pageable pageable);

    @Query("SELECT new com.esand.orders.web.dto.OrderResponseDto(p.id, null, p.cpf, null, p.sku, p.price, p.quantity, p.total, p.processing, p.date) FROM Order p WHERE p.sku = :sku")
    Page<OrderResponseDto> findAllBySku(@Param("sku") String sku, Pageable pageable);

    @Query("SELECT new com.esand.orders.web.dto.OrderResponseDto(p.id, null, p.cpf, null, p.sku, p.price, p.quantity, p.total, p.processing, p.date) FROM Order p WHERE p.cpf = :cpf AND p.date > :date")
    Page<OrderResponseDto> findAllByCpfAndDateAfter(@Param("cpf") String cpf, @Param("date") LocalDateTime date, Pageable pageable);

    @Query("SELECT new com.esand.orders.web.dto.OrderResponseDto(p.id, null, p.cpf, null, p.sku, p.price, p.quantity, p.total, p.processing, p.date) FROM Order p WHERE p.cpf = :cpf AND p.date < :date")
    Page<OrderResponseDto> findAllByCpfAndDateBefore(@Param("cpf") String cpf, @Param("date") LocalDateTime date, Pageable pageable);

    @Query("SELECT new com.esand.orders.web.dto.OrderResponseDto(p.id, null, p.cpf, null, p.sku, p.price, p.quantity, p.total, p.processing, p.date) FROM Order p WHERE p.cpf = :cpf AND p.date BETWEEN :afterDate AND :beforeDate")
    Page<OrderResponseDto> findAllByCpfAndDateBetween(@Param("cpf") String cpf, @Param("afterDate") LocalDateTime afterDate, @Param("beforeDate") LocalDateTime beforeDate, Pageable pageable);

    @Query("SELECT new com.esand.orders.web.dto.OrderResponseDto(p.id, null, p.cpf, null, p.sku, p.price, p.quantity, p.total, p.processing, p.date) FROM Order p WHERE p.cpf = :cpf")
    Page<OrderResponseDto> findAllByCpf(@Param("cpf") String cpf, Pageable pageable);

    @Query("SELECT new com.esand.orders.web.dto.OrderResponseDto(p.id, null, p.cpf, null, p.sku, p.price, p.quantity, p.total, p.processing, p.date) FROM Order p WHERE p.date > :date")
    Page<OrderResponseDto> findByDateAfter(@Param("date") LocalDateTime date, Pageable pageable);

    @Query("SELECT new com.esand.orders.web.dto.OrderResponseDto(p.id, null, p.cpf, null, p.sku, p.price, p.quantity, p.total, p.processing, p.date) FROM Order p WHERE p.date < :date")
    Page<OrderResponseDto> findByDateBefore(@Param("date") LocalDateTime date, Pageable pageable);

    @Query("SELECT new com.esand.orders.web.dto.OrderResponseDto(p.id, null, p.cpf, null, p.sku, p.price, p.quantity, p.total, p.processing, p.date) FROM Order p WHERE p.date BETWEEN :afterDate AND :beforeDate")
    Page<OrderResponseDto> findByDateBetween(@Param("afterDate") LocalDateTime afterDate, @Param("beforeDate") LocalDateTime beforeDate, Pageable pageable);
}
