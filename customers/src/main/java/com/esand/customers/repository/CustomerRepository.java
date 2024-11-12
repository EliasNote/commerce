package com.esand.customers.repository;

import com.esand.customers.entity.Customer;
import com.esand.customers.web.dto.CustomerResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    @Query("SELECT new com.esand.customers.web.dto.CustomerResponseDto(c.name, c.cpf, c.phone, c.email, c.address, c.birthDate, c.gender, c.createDate) " +
            "FROM Customer c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<CustomerResponseDto> findByNameIgnoreCaseContaining(@Param("name") String name, Pageable pageable);

    Optional<Customer> findByCpf(String cpf);

    @Query("SELECT new com.esand.customers.web.dto.CustomerResponseDto(c.name, c.cpf, c.phone, c.email, c.address, c.birthDate, c.gender, c.createDate) " +
            "FROM Customer c")
    Page<CustomerResponseDto> findAllPageable(Pageable pageable);

    @Query("SELECT new com.esand.customers.web.dto.CustomerResponseDto(c.name, c.cpf, c.phone, c.email, c.address, c.birthDate, c.gender, c.createDate) " +
            "FROM Customer c WHERE c.createDate > :date")
    Page<CustomerResponseDto> findByCreateDateAfter(@Param("date") LocalDateTime date, Pageable pageable);

    @Query("SELECT new com.esand.customers.web.dto.CustomerResponseDto(c.name, c.cpf, c.phone, c.email, c.address, c.birthDate, c.gender, c.createDate) " +
            "FROM Customer c WHERE c.createDate < :date")
    Page<CustomerResponseDto> findByCreateDateBefore(@Param("date") LocalDateTime date, Pageable pageable);

    @Query("SELECT new com.esand.customers.web.dto.CustomerResponseDto(c.name, c.cpf, c.phone, c.email, c.address, c.birthDate, c.gender, c.createDate) " +
            "FROM Customer c WHERE c.createDate BETWEEN :afterDate AND :beforeDate")
    Page<CustomerResponseDto> findByCreateDateBetween(@Param("afterDate") LocalDateTime afterDate, @Param("beforeDate") LocalDateTime beforeDate, Pageable pageable);

    void deleteByCpf(String cpf);

    boolean existsByCpf(String cpf);

    @Query("SELECT new com.esand.customers.web.dto.CustomerResponseDto(c.name, c.cpf, c.phone, c.email, c.address, c.birthDate, c.gender, c.createDate) " +
            "FROM Customer c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND c.createDate BETWEEN :after AND :before")
    Page<CustomerResponseDto> findByNameIgnoreCaseContainingAndCreateDateBetween(
            @Param("name") String name,
            @Param("after") LocalDateTime after,
            @Param("before") LocalDateTime before,
            Pageable pageable
    );

    @Query("SELECT new com.esand.customers.web.dto.CustomerResponseDto(c.name, c.cpf, c.phone, c.email, c.address, c.birthDate, c.gender, c.createDate) " +
            "FROM Customer c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND c.createDate > :after")
    Page<CustomerResponseDto> findByNameIgnoreCaseContainingAndCreateDateAfter(
            @Param("name") String name,
            @Param("after") LocalDateTime after,
            Pageable pageable
    );

    @Query("SELECT new com.esand.customers.web.dto.CustomerResponseDto(c.name, c.cpf, c.phone, c.email, c.address, c.birthDate, c.gender, c.createDate) " +
            "FROM Customer c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND c.createDate < :before")
    Page<CustomerResponseDto> findByNameIgnoreCaseContainingAndCreateDateBefore(
            @Param("name") String name,
            @Param("before") LocalDateTime before,
            Pageable pageable
    );
}
