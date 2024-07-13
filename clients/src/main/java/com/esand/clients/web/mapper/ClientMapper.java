package com.esand.clients.web.mapper;

import com.esand.clients.entity.Client;
import com.esand.clients.web.dto.ClientCreateDto;
import com.esand.clients.web.dto.ClientResponseDto;
import com.esand.clients.web.dto.ClientUpdateDto;
import com.esand.clients.web.dto.PageableDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ClientMapper {
    ClientMapper INSTANCE = Mappers.getMapper(ClientMapper.class);

    Client toClient(ClientCreateDto dto);

    ClientResponseDto toDto(Client client);

    void updateClient(ClientUpdateDto dto, @MappingTarget Client client);

    PageableDto toPageableDto(Page page);
}
