package com.esand.clients.repository;

import com.esand.clients.entity.Client;
import com.esand.clients.repository.pagination.ClientDtoPagination;
import com.esand.clients.web.dto.PageableDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Page<ClientDtoPagination> findByNameIgnoreCaseContaining(String name, Pageable pageable);

    Optional<Client> findByCpf(String cpf);

    @Query("SELECT c FROM Client c")
    Page<ClientDtoPagination> findAllPageable(Pageable pageable);

    Page<ClientDtoPagination> findByCreateDateAfter(LocalDateTime date, Pageable pageable);

    Page<ClientDtoPagination> findByCreateDateBefore(LocalDateTime date, Pageable pageable);

    Page<ClientDtoPagination> findByCreateDateBetween(LocalDateTime afterDate, LocalDateTime beforeDate, Pageable pageable);

    void deleteByCpf(String cpf);

    boolean existsByCpf(String cpf);
}
