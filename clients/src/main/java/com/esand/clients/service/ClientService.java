package com.esand.clients.service;

import com.esand.clients.entity.Client;
import com.esand.clients.repository.ClientRepository;
import com.esand.clients.web.dto.ClientCreateDto;
import com.esand.clients.web.dto.ClientResponseDto;
import com.esand.clients.web.dto.PageableDto;
import com.esand.clients.web.mapper.ClientMapper;
import lombok.RequiredArgsConstructor;
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
        Client client = clientRepository.save(clientMapper.toClient(dto));
        return clientMapper.toDto(client);
    }

    @Transactional(readOnly = true)
    public PageableDto findAll(Pageable pageable) {
        return clientMapper.toPageableDto(clientRepository.findAllPageable(pageable));
    }

    @Transactional(readOnly = true)
    public ClientResponseDto findByName(String name) {
        return clientMapper.toDto(clientRepository.findByNameIgnoreCase(name).orElseThrow());
    }

    @Transactional(readOnly = true)
    public ClientResponseDto findByCpf(String cpf) {
        return clientMapper.toDto(findClientByCpf(cpf));
    }

    @Transactional
    public void update(String cpf, ClientCreateDto dto) {
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
        return clientRepository.findByCpfIgnoreCase(cpf).orElseThrow(
                () -> new RuntimeException("Cliente não encontrado")
        );
    }
}
