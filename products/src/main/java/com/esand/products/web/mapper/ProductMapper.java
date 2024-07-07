package com.esand.products.web.mapper;

import com.esand.products.entity.Product;
import com.esand.products.web.dto.PageableDto;
import com.esand.products.web.dto.ProductCreateDto;
import com.esand.products.web.dto.ProductResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    Product toProduct(ProductCreateDto dto);

    ProductResponseDto toDto(Product product);

    void updateProduct(ProductCreateDto dto, @MappingTarget Product product);

    PageableDto toPageableDto(Page page);
}
