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
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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
        PageableDto dto = clientMapper.toPageableDto(clientRepository.findAllPageable(pageable));
        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No customers found");
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public PageableDto findByName(Pageable pageable, String name) {
        PageableDto dto = clientMapper.toPageableDto(clientRepository.findByNameIgnoreCaseContaining(name, pageable));
        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("Customer not found by name");
        }
        return dto;
    }

    @Transactional(readOnly = true)
    public ClientResponseDto findByCpf(String cpf) {
        return clientMapper.toDto(findClientByCpf(cpf));
    }

    @Transactional(readOnly = true)
    public PageableDto findClientsByDate(String afterDate, String beforeDate, Pageable pageable) {
        PageableDto dto;
        if (afterDate != null && beforeDate != null) {
            dto = clientMapper.toPageableDto(clientRepository.findByCreateDateBetween(LocalDate.parse(afterDate).atStartOfDay(), LocalDate.parse(beforeDate).atStartOfDay().plusDays(1), pageable));
        } else if (afterDate != null) {
            dto = clientMapper.toPageableDto(clientRepository.findByCreateDateAfter(LocalDate.parse(afterDate).atStartOfDay(), pageable));
        } else if (beforeDate != null) {
            dto = clientMapper.toPageableDto(clientRepository.findByCreateDateBefore(LocalDate.parse(beforeDate).atStartOfDay().plusDays(1), pageable));
        } else {
            throw new EntityNotFoundException("No date parameters provided");
        }

        if (dto.getContent().isEmpty()) {
            throw new EntityNotFoundException("No clients found by date(s)");
        }

        return dto;
    }

    @Transactional
    public void update(String cpf, ClientUpdateDto dto) {
        Client client = findClientByCpf(cpf);
        clientMapper.updateClient(dto, client);
    }

    @Transactional
    public void deleteByCpf(String cpf) {
        if (!clientRepository.existsByCpf(cpf)) {
            throw new EntityNotFoundException("Customer not found by CPF");
        }
        clientRepository.deleteByCpf(cpf);
    }

    private Client findClientByCpf(String cpf) {
        return clientRepository.findByCpf(cpf).orElseThrow(
                () -> new EntityNotFoundException("Customer not found by CPF")
        );
    }
}
