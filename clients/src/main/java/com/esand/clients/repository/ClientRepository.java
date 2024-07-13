package com.esand.clients.repository;

import com.esand.clients.entity.Client;
import com.esand.clients.repository.pagination.ClientDtoPagination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByNameIgnoreCase(String name);

    Optional<Client> findByCpf(String cpf);

    @Query("SELECT c FROM Client c")
    Page<ClientDtoPagination> findAllPageable(Pageable pageable);
}
