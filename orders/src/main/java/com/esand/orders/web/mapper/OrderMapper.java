package com.esand.orders.web.mapper;

import com.esand.orders.client.customers.Customer;
import com.esand.orders.client.products.Product;
import com.esand.orders.entity.Order;
import com.esand.orders.web.dto.OrderResponseDto;
import com.esand.orders.web.dto.PageableDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    Order toOrder(Customer customer, Product product);

    OrderResponseDto toDto(Order order);

    PageableDto toPageableDto(Page page);


}
