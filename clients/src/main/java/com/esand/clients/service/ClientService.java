package com.esand.clients.service;

import com.esand.clients.entity.Client;
import com.esand.clients.exception.CpfUniqueViolationException;
import com.esand.clients.exception.EntityNotFoundException;
import com.esand.clients.repository.ClientRepository;
import com.esand.clients.web.dto.ClientCreateDto;
import com.esand.clients.web.dto.ClientResponseDto;
import com.esand.clients.web.dto.ClientUpdateDto;
import com.esand.clients.web.dto.PageableDto;
import com.esand.clients.web.mapper.ClientMapper;
import jakarta.ws.rs.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ClientService {
    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Transactional
    public ClientResponseDto save(ClientCreateDto dto) {
        try {
            Client client = clientRepository.save(clientMapper.toClient(dto));
            return clientMapper.toDto(client);
        } catch (DataIntegrityViolationException ex) {
            throw new CpfUniqueViolationException(String.format("CPF %s cannot be registered, there is already a registered customer with an informed CPF", dto.getCpf()));
        }
    }

    @Transactional(readOnly = true)
    public PageableDto findAll(Pageable pageable) {
        return clientMapper.toPageableDto(clientRepository.findAllPageable(pageable));
    }

    @Transactional(readOnly = true)
    public ClientResponseDto findByName(String name) {
        return clientMapper.toDto(clientRepository.findByNameIgnoreCase(name).orElseThrow(
                () -> new EntityNotFoundException("Customer not found by name")
        ));
    }

    @Transactional(readOnly = true)
    public ClientResponseDto findByCpf(String cpf) {
        return clientMapper.toDto(findClientByCpf(cpf));
    }

    @Transactional
    public void update(String cpf, ClientUpdateDto dto) {
        Client client = findClientByCpf(cpf);
        clientMapper.updateClient(dto, client);
    }

    @Transactional
    public String alter(String cpf) {
        Client client = findClientByCpf(cpf);
        client.setStatus(!client.getStatus());
        return client.getStatus().toString();
    }

    private Client findClientByCpf(String cpf) {
        return clientRepository.findByCpf(cpf).orElseThrow(
                () -> new EntityNotFoundException("Customer not found by CPF")
        );
    }
}
