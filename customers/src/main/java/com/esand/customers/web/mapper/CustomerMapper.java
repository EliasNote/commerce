package com.esand.customers.web.mapper;

import com.esand.customers.entity.Customer;
import com.esand.customers.web.dto.CustomerCreateDto;
import com.esand.customers.web.dto.CustomerResponseDto;
import com.esand.customers.web.dto.CustomerUpdateDto;
import com.esand.customers.web.dto.PageableDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CustomerMapper {
    CustomerMapper INSTANCE = Mappers.getMapper(CustomerMapper.class);

    Customer toCustomer(CustomerCreateDto dto);

    CustomerResponseDto toDto(Customer customer);

    void updateCustomer(CustomerUpdateDto dto, @MappingTarget Customer customer);

    PageableDto toPageableDto(Page page);
}
