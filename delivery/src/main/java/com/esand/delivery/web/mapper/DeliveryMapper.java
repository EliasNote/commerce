package com.esand.delivery.web.mapper;

import com.esand.delivery.entity.Delivery;
import com.esand.delivery.web.dto.DeliveryResponseDto;
import com.esand.delivery.web.dto.DeliverySaveDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeliveryMapper {
    DeliveryMapper INSTANCE = Mappers.getMapper(DeliveryMapper.class);

    Delivery toDelivery(DeliverySaveDto dto);

    DeliveryResponseDto toDto(Delivery delivery);
}
