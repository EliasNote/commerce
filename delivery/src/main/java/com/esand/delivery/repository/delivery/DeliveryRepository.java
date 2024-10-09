package com.esand.delivery.repository.delivery;

import com.esand.delivery.entity.Delivery;
import com.esand.delivery.repository.pagination.DeliveryDtoPagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

    void deleteAllByStatus(Delivery.Status status);

    boolean existsByStatus(Delivery.Status status);

    @Query("SELECT new com.esand.delivery.repository.pagination.DeliveryDtoPagination(d.id, null, d.cpf, null, d.sku, d.price, d.quantity, d.total, d.status, d.date) " +
            "FROM Delivery d WHERE d.date > :date")
    Page<DeliveryDtoPagination> findAllByDateAfter(@Param("date") LocalDateTime date, Pageable pageable);

    @Query("SELECT new com.esand.delivery.repository.pagination.DeliveryDtoPagination(d.id, null, d.cpf, null, d.sku, d.price, d.quantity, d.total, d.status, d.date) " +
            "FROM Delivery d WHERE d.date < :date")
    Page<DeliveryDtoPagination> findAllByDateBefore(@Param("date") LocalDateTime date, Pageable pageable);

    @Query("SELECT new com.esand.delivery.repository.pagination.DeliveryDtoPagination(d.id, null, d.cpf, null, d.sku, d.price, d.quantity, d.total, d.status, d.date) " +
            "FROM Delivery d WHERE d.date BETWEEN :afterDate AND :beforeDate")
    Page<DeliveryDtoPagination> findAllByDateBetween(@Param("afterDate") LocalDateTime afterDate, @Param("beforeDate") LocalDateTime beforeDate, Pageable pageable);

    @Query("SELECT new com.esand.delivery.repository.pagination.DeliveryDtoPagination(d.id, null, d.cpf, null, d.sku, d.price, d.quantity, d.total, d.status, d.date) " +
            "FROM Delivery d")
    Page<DeliveryDtoPagination> findAllPageable(Pageable pageable);

    @Query("SELECT new com.esand.delivery.repository.pagination.DeliveryDtoPagination(d.id, null, d.cpf, null, d.sku, d.price, d.quantity, d.total, d.status, d.date) " +
            "FROM Delivery d WHERE d.status = :status AND d.date BETWEEN :localDateTime AND :localDateTime1")
    Page<DeliveryDtoPagination> findAllByStatusAndDateBetween(@Param("status") Delivery.Status status,
                                                              @Param("localDateTime") LocalDateTime localDateTime,
                                                              @Param("localDateTime1") LocalDateTime localDateTime1,
                                                              Pageable pageable);

    @Query("SELECT new com.esand.delivery.repository.pagination.DeliveryDtoPagination(d.id, null, d.cpf, null, d.sku, d.price, d.quantity, d.total, d.status, d.date) " +
            "FROM Delivery d WHERE d.status = :status AND d.date > :localDateTime")
    Page<DeliveryDtoPagination> findAllByStatusAndDateAfter(@Param("status") Delivery.Status status,
                                                            @Param("localDateTime") LocalDateTime localDateTime,
                                                            Pageable pageable);

    @Query("SELECT new com.esand.delivery.repository.pagination.DeliveryDtoPagination(d.id, null, d.cpf, null, d.sku, d.price, d.quantity, d.total, d.status, d.date) " +
            "FROM Delivery d WHERE d.status = :status AND d.date < :localDateTime")
    Page<DeliveryDtoPagination> findAllByStatusAndDateBefore(@Param("status") Delivery.Status status,
                                                             @Param("localDateTime") LocalDateTime localDateTime,
                                                             Pageable pageable);

    @Query("SELECT new com.esand.delivery.repository.pagination.DeliveryDtoPagination(d.id, null, d.cpf, null, d.sku, d.price, d.quantity, d.total, d.status, d.date) " +
            "FROM Delivery d WHERE d.status = :status")
    Page<DeliveryDtoPagination> findAllByStatus(@Param("status") Delivery.Status status, Pageable pageable);

    List<Delivery> findAllByStatusAndDateBetween(Delivery.Status status, LocalDateTime localDateTime, LocalDateTime localDateTime1);

    List<Delivery> findAllByStatusAndDateAfter(Delivery.Status status, LocalDateTime localDateTime);

    List<Delivery> findAllByStatusAndDateBefore(Delivery.Status status, LocalDateTime localDateTime);

    List<Delivery> findAllByStatus(Delivery.Status status);

    @Query("SELECT new com.esand.delivery.repository.pagination.DeliveryDtoPagination(d.id, null, d.cpf, null, d.sku, d.price, d.quantity, d.total, d.status, d.date) " +
            "FROM Delivery d WHERE d.cpf = :cpf AND d.date BETWEEN :localDateTime AND :localDateTime1")
    Page<DeliveryDtoPagination> findAllByCpfAndDateBetween(@Param("cpf") String cpf,
                                                           @Param("localDateTime") LocalDateTime localDateTime,
                                                           @Param("localDateTime1") LocalDateTime localDateTime1,
                                                           Pageable pageable);

    @Query("SELECT new com.esand.delivery.repository.pagination.DeliveryDtoPagination(d.id, null, d.cpf, null, d.sku, d.price, d.quantity, d.total, d.status, d.date) " +
            "FROM Delivery d WHERE d.cpf = :cpf AND d.date > :localDateTime")
    Page<DeliveryDtoPagination> findAllByCpfAndDateAfter(@Param("cpf") String cpf,
                                                         @Param("localDateTime") LocalDateTime localDateTime,
                                                         Pageable pageable);

    @Query("SELECT new com.esand.delivery.repository.pagination.DeliveryDtoPagination(d.id, null, d.cpf, null, d.sku, d.price, d.quantity, d.total, d.status, d.date) " +
            "FROM Delivery d WHERE d.cpf = :cpf AND d.date < :localDateTime")
    Page<DeliveryDtoPagination> findAllByCpfAndDateBefore(@Param("cpf") String cpf,
                                                          @Param("localDateTime") LocalDateTime localDateTime,
                                                          Pageable pageable);

    @Query("SELECT new com.esand.delivery.repository.pagination.DeliveryDtoPagination(d.id, null, d.cpf, null, d.sku, d.price, d.quantity, d.total, d.status, d.date) " +
            "FROM Delivery d WHERE d.cpf = :cpf")
    Page<DeliveryDtoPagination> findAllByCpf(@Param("cpf") String cpf, Pageable pageable);

    @Query("SELECT new com.esand.delivery.repository.pagination.DeliveryDtoPagination(d.id, null, d.cpf, null, d.sku, d.price, d.quantity, d.total, d.status, d.date) " +
            "FROM Delivery d WHERE d.sku = :sku AND d.date BETWEEN :localDateTime AND :localDateTime1")
    Page<DeliveryDtoPagination> findAllBySkuAndDateBetween(@Param("sku") String sku,
                                                           @Param("localDateTime") LocalDateTime localDateTime,
                                                           @Param("localDateTime1") LocalDateTime localDateTime1,
                                                           Pageable pageable);

    @Query("SELECT new com.esand.delivery.repository.pagination.DeliveryDtoPagination(d.id, null, d.cpf, null, d.sku, d.price, d.quantity, d.total, d.status, d.date) " +
            "FROM Delivery d WHERE d.sku = :sku AND d.date > :localDateTime")
    Page<DeliveryDtoPagination> findAllBySkuAndDateAfter(@Param("sku") String sku,
                                                         @Param("localDateTime") LocalDateTime localDateTime,
                                                         Pageable pageable);

    @Query("SELECT new com.esand.delivery.repository.pagination.DeliveryDtoPagination(d.id, null, d.cpf, null, d.sku, d.price, d.quantity, d.total, d.status, d.date) " +
            "FROM Delivery d WHERE d.sku = :sku AND d.date < :localDateTime")
    Page<DeliveryDtoPagination> findAllBySkuAndDateBefore(@Param("sku") String sku,
                                                          @Param("localDateTime") LocalDateTime localDateTime,
                                                          Pageable pageable);

    @Query("SELECT new com.esand.delivery.repository.pagination.DeliveryDtoPagination(d.id, null, d.cpf, null, d.sku, d.price, d.quantity, d.total, d.status, d.date) " +
            "FROM Delivery d WHERE d.sku = :sku")
    Page<DeliveryDtoPagination> findAllBySku(@Param("sku") String sku, Pageable pageable);
}
