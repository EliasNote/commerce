package com.esand.customers.repository;

import com.esand.customers.entity.Customer;
import com.esand.customers.repository.pagination.CustomerDtoPagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Page<CustomerDtoPagination> findByNameIgnoreCaseContaining(String name, Pageable pageable);

    Optional<Customer> findByCpf(String cpf);

    @Query("SELECT c FROM Customer c")
    Page<CustomerDtoPagination> findAllPageable(Pageable pageable);

    Page<CustomerDtoPagination> findByCreateDateAfter(LocalDateTime date, Pageable pageable);

    Page<CustomerDtoPagination> findByCreateDateBefore(LocalDateTime date, Pageable pageable);

    Page<CustomerDtoPagination> findByCreateDateBetween(LocalDateTime afterDate, LocalDateTime beforeDate, Pageable pageable);

    void deleteByCpf(String cpf);

    boolean existsByCpf(String cpf);

    Page<CustomerDtoPagination> findByNameIgnoreCaseContainingAndCreateDateBetween(String name, LocalDateTime after, LocalDateTime before, Pageable pageable);

    Page<CustomerDtoPagination> findByNameIgnoreCaseContainingAndCreateDateAfter(String name, LocalDateTime after, Pageable pageable);

    Page<CustomerDtoPagination> findByNameIgnoreCaseContainingAndCreateDateBefore(String name, LocalDateTime before, Pageable pageable);
}
