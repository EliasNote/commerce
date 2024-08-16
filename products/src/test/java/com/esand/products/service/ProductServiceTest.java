package com.esand.products.service;

import com.esand.products.entity.EntityMock;
import com.esand.products.entity.Product;
import com.esand.products.exception.*;
import com.esand.products.repository.ProductRepository;
import com.esand.products.repository.pagination.ProductDtoPagination;
import com.esand.products.web.dto.PageableDto;
import com.esand.products.web.dto.ProductCreateDto;
import com.esand.products.web.dto.ProductResponseDto;
import com.esand.products.web.dto.ProductUpdateDto;
import com.esand.products.web.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveProductSuccess() {
        ProductCreateDto createDto = EntityMock.createDto();
        Product product = EntityMock.product();
        ProductResponseDto responseDto = EntityMock.productResponseDto();

        when(productMapper.toProduct(any(ProductCreateDto.class))).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(any(Product.class))).thenReturn(responseDto);

        ProductResponseDto response = productService.save(createDto);

        assertNotNull(response);
        assertEquals("Wireless MouseS", response.getTitle());
        assertEquals("A high precision wireless m", response.getDescription());
        assertEquals(29.99, response.getPrice());
        assertEquals("MOUSES", response.getCategory());
        assertEquals(10, response.getQuantity());
        assertEquals("MOUSE-2024-WL-0010", response.getSku());
        assertTrue(response.getStatus());
    }

    @Test
    void testSaveProductTitleUniqueViolationException() {
        ProductCreateDto createDto = EntityMock.createDto();

        when(productRepository.existsByTitle(any(String.class))).thenReturn(true);

        assertThrows(TitleUniqueViolationException.class, () -> productService.save(createDto));
    }

    @Test
    void testSaveProductSkuUniqueViolationException() {
        ProductCreateDto createDto = EntityMock.createDto();

        when(productRepository.existsBySku(any(String.class))).thenReturn(true);

        assertThrows(SkuUniqueViolationException.class, () -> productService.save(createDto));
    }

    @Test
    void testFindAllSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(productRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findAll(page.getPageable());

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindAllProductsEntityNotFoundException() {
        Page<ProductDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(productRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findAll(page.getPageable()));
    }

    @Test
    void testFindByTitleSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(productRepository.findByTitleIgnoreCaseContaining(any(Pageable.class), any(String.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findByTitle(page.getPageable(), "Wireless MouseS");

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindByTitleEntityNotFoundException() {
        Page<ProductDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(productRepository.findByTitleIgnoreCaseContaining(any(Pageable.class), any(String.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findByTitle(page.getPageable(), "Wireless MouseS"));
    }

    @Test
    void testFindBySupplierSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(productRepository.findBySupplierIgnoreCaseContaining(any(Pageable.class), any(String.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findBySupplier(page.getPageable(), "Wireless MouseS");

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindBySupplierEntityNotFoundException() {
        Page<ProductDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(productRepository.findBySupplierIgnoreCaseContaining(any(Pageable.class), any(String.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findBySupplier(page.getPageable(), "Wireless MouseS"));
    }

    @Test
    void testFindByCategory() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(productRepository.findByCategory(any(Pageable.class), any(Product.Category.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findByCategory(page.getPageable(), "MOUSES");

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindByCategoryNotFound() {
        Page<ProductDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(productRepository.findByCategory(any(Pageable.class), any(Product.Category.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findByCategory(page.getPageable(), "MOUSES"));
    }

    @Test
    void testFindByCategoryInvalidCategoryException() {
        Pageable pageable = PageRequest.of(0, 10);

        when(productRepository.findByCategory(any(Pageable.class), any(Product.Category.class))).thenThrow(InvalidCategoryException.class);

        assertThrows(InvalidCategoryException.class, () -> productService.findByCategory(pageable, "MOUSE"));
    }

    @Test
    void testFindBySkuSuccess() {
        Product product = EntityMock.product();
        ProductResponseDto responseDto = EntityMock.productResponseDto();

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));
        when(productMapper.toDto(any(Product.class))).thenReturn(responseDto);

        ProductResponseDto response = productService.findBySku("MOUSE-2024-WL-0010");

        assertNotNull(response);
        assertEquals("Wireless MouseS", response.getTitle());
        assertEquals("A high precision wireless m", response.getDescription());
        assertEquals(29.99, response.getPrice());
        assertEquals("MOUSES", response.getCategory());
        assertEquals(10, response.getQuantity());
        assertEquals("MOUSE-2024-WL-0010", response.getSku());
        assertTrue(response.getStatus());
    }

    @Test
    void testFindBySkuEntityNotFoundException() {
        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.findBySku("MOUSE-2024-WL-0010"));
    }

    @Test
    void testFindAllActivedSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(productRepository.findAllByStatus(any(Pageable.class), eq(true))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findAllActived(page.getPageable());

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindAllActivedEntityNotFoundException() {
        Page<ProductDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(productRepository.findAllByStatus(any(Pageable.class), eq(true))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findAllActived(page.getPageable()));
    }

    @Test
    void testFindAllDisabledSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(productRepository.findAllByStatus(any(Pageable.class), eq(false))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findAllDisabled(page.getPageable());

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindAllDisabledEntityNotFoundException() {
        Page<ProductDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(productRepository.findAllByStatus(any(Pageable.class), eq(false))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findAllDisabled(page.getPageable()));
    }

    @Test
    void testFindProductsByDateBetweenSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(productRepository.findByCreateDateBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findProductsByDate(LocalDate.now().minusDays(1).toString(), LocalDate.now().plusDays(1).toString(), page.getPageable());

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindProductsByDateAfterSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(productRepository.findByCreateDateAfter(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findProductsByDate(LocalDate.now().minusDays(1).toString(), null, page.getPageable());

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindProductsByDateBeforeSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(productRepository.findByCreateDateBefore(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findProductsByDate(null, LocalDate.now().plusDays(1).toString(), page.getPageable());

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindProductsByDateNoDateParametersProvided() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(EntityNotFoundException.class, () -> productService.findProductsByDate(null, null, pageable));
    }

    @Test
    void testFindProductsByDateEntityNotFoundException() {
        Page<ProductDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(productRepository.findByCreateDateBefore(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findProductsByDate(null, LocalDate.now().plusDays(1).toString(), page.getPageable()));
    }

    @Test
    void testUpdateBySkuSuccess() {
        Product product = EntityMock.product();
        ProductUpdateDto updateDto = EntityMock.productUpdateDto();

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));
        doNothing().when(productMapper).updateProduct(any(ProductUpdateDto.class), any(Product.class));

        productService.update("MOUSE-2024-WL-0010", updateDto);
    }

    @Test
    void testUpdateBySkuEntityNotFoundException() {
        ProductUpdateDto updateDto = EntityMock.productUpdateDto();

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.update("MOUSE-2024-WL-0010", updateDto));
    }

    @Test
    void testAlterStatusBySkuSuccess() {
        Product product = EntityMock.product();

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));

        String response = productService.alter("MOUSE-2024-WL-0010");

        assertNotNull(response);
        assertEquals("false", response);
    }

    @Test
    void testAlterStatusBySkuInvalidProductStatusException() {
        Product product = EntityMock.product();
        product.setQuantity(0);

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));

        assertThrows(InvalidProductStatusException.class, () -> productService.alter("MOUSE-2024-WL-0010"));
    }

    @Test
    void testAddProductBySkuSuccess() {
        Product product = EntityMock.product();

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));

        String response = productService.add("MOUSE-2024-WL-0010", 1);

        assertNotNull(response);
        assertEquals("11", response);
    }

    @Test
    void testAddProductBySkuEntityNotFoundException() {
        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.add("MOUSE-2024-WL-0010", 1));
    }

    @Test
    void testAddProductBySkuInvalidQuantityException() {
        Product product = EntityMock.product();

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));

        assertThrows(InvalidQuantityException.class, () -> productService.add("MOUSE-2024-WL-0010", 0));
    }

    @Test
    void testSubProductBySkuSuccess() {
        Product product = EntityMock.product();

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));

        String response = productService.sub("MOUSE-2024-WL-0010", 1);

        assertNotNull(response);
        assertEquals("9", response);
    }

    @Test
    void testSubProductBySkuEntityNotFoundException() {
        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.sub("MOUSE-2024-WL-0010", 1));
    }

    @Test
    void testSubProductBySkuInvalidQuantityException() {
        Product product = EntityMock.product();

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));

        assertThrows(InvalidQuantityException.class, () -> productService.sub("MOUSE-2024-WL-0010", 0));
    }

    @Test
    void testSubProductBySkuInvalidQuantityAvailableException() {
        Product product = EntityMock.product();

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));

        assertThrows(InvalidQuantityException.class, () -> productService.sub("MOUSE-2024-WL-0010", 11));
    }

    @Test
    void testDeleteProductBySkuSuccess() {
        when(productRepository.existsBySku(any(String.class))).thenReturn(true);

        productService.deleteBySku("MOUSE-2024-WL-0010");
    }

    @Test
    void testDeleteProductBySkuEntityNotFoundException() {
        when(productRepository.existsBySku(any(String.class))).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> productService.deleteBySku("MOUSE-2024-WL-0010"));
    }
}