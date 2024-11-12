package com.esand.products.service;

import com.esand.products.entity.Category;
import com.esand.products.entity.EntityMock;
import com.esand.products.entity.Product;
import com.esand.products.exception.*;
import com.esand.products.repository.CategoryRepository;
import com.esand.products.repository.ProductRepository;
import com.esand.products.repository.pagination.ProductDtoPagination;
import com.esand.products.web.dto.PageableDto;
import com.esand.products.web.dto.ProductCreateDto;
import com.esand.products.web.dto.ProductResponseDto;
import com.esand.products.web.dto.ProductUpdateDto;
import com.esand.products.web.mapper.ProductMapper;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    void verifyResultPagination(ProductDtoPagination response, ProductDtoPagination expect) {
        assertNotNull(response);
        assertEquals(response.getTitle(), expect.getTitle());
        assertEquals(response.getDescription(), expect.getDescription());
        assertEquals(response.getPrice(), expect.getPrice());
        assertEquals(response.getCategories().get(0).getName(), expect.getCategories().get(0).getName());
        assertEquals(response.getQuantity(), expect.getQuantity());
        assertEquals(response.getSku(), expect.getSku());
        assertEquals(response.getStatus(), expect.getStatus());
    }

    void verifyResultDto(ProductResponseDto response, ProductDtoPagination expect) {
        assertNotNull(response);
        assertEquals(response.getTitle(), expect.getTitle());
        assertEquals(response.getDescription(), expect.getDescription());
        assertEquals(response.getPrice(), expect.getPrice());
        assertEquals(response.getCategories().get(0).getName(), expect.getCategories().get(0).getName());
        assertEquals(response.getQuantity(), expect.getQuantity());
        assertEquals(response.getSku(), expect.getSku());
        assertEquals(response.getStatus(), expect.getStatus());
    }

    void verifyResult(Object object, ProductDtoPagination expect) {
        if (object instanceof PageableDto) {
            PageableDto page = (PageableDto) object;
            assertNotNull(page);
            assertNotNull(page.getContent());
            assertEquals(1, page.getContent().size());

            verifyResultPagination((ProductDtoPagination) page.getContent().getFirst(), expect);
        } else {
            verifyResultDto((ProductResponseDto) object, expect);
        }
    }

    @Test
    void testSaveProductSuccess() {
        ProductCreateDto createDto = EntityMock.createDto();
        Product product = EntityMock.product();
        ProductResponseDto responseDto = EntityMock.productResponseDto();
        Category category = EntityMock.category();

        when(productMapper.toProduct(any(ProductCreateDto.class))).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDto(any(Product.class))).thenReturn(responseDto);
        when(categoryService.findByName(any(String.class))).thenReturn(category);

        ProductResponseDto response = productService.save(createDto);

        verifyResult(response, EntityMock.productDtoPaginationMock());
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
    void testSaveProductEntityNotFoundException() {
        ProductCreateDto createDto = EntityMock.createDto();

        when(categoryService.findByName(any(String.class))).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> productService.save(createDto));
    }

    @Test
    void testFindAllSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(productRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findAll(null, null, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindAllProductsAndDateBetweenSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String after = LocalDate.now().minusDays(1).toString();
        String before = LocalDate.now().plusDays(1).toString();

        when(productRepository.findByCreateDateBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findAll(after, before, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindAllProductsAndDateAfterSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String after = LocalDate.now().minusDays(1).toString();

        when(productRepository.findByCreateDateAfter(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findAll(after, null, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindAllProductsAndDateBeforeSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String before = LocalDate.now().plusDays(1).toString();

        when(productRepository.findByCreateDateBefore(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findAll(null, before, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindAllProductsEntityNotFoundException() {
        Page<ProductDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(productRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findAll(null, null, page.getPageable()));
    }

    @Test
    void testFindByTitleSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(productRepository.findByTitleIgnoreCaseContaining(any(String.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findByTitle(EntityMock.PRODUCT_TITLE, null, null, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindByTitleProductsAndDateBetweenSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String after = LocalDate.now().minusDays(1).toString();
        String before = LocalDate.now().plusDays(1).toString();

        when(productRepository.findByTitleIgnoreCaseContainingAndCreateDateBetween(any(String.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findByTitle(EntityMock.PRODUCT_TITLE, after, before, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindByTitleProductsAndDateAfterSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String after = LocalDate.now().minusDays(1).toString();

        when(productRepository.findByTitleIgnoreCaseContainingAndCreateDateAfter(any(String.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findByTitle(EntityMock.PRODUCT_TITLE, after, null, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindByTitleProductsAndDateBeforeSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String before = LocalDate.now().plusDays(1).toString();

        when(productRepository.findByTitleIgnoreCaseContainingAndCreateDateBefore(any(String.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findByTitle(EntityMock.PRODUCT_TITLE, null, before, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindByTitleEntityNotFoundException() {
        Page<ProductDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(productRepository.findByTitleIgnoreCaseContaining(any(String.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findByTitle(EntityMock.PRODUCT_TITLE, null, null, page.getPageable()));
    }

    @Test
    void testFindBySupplierSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(productRepository.findBySupplierIgnoreCaseContaining(any(String.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findBySupplier(EntityMock.PRODUCT_TITLE, null, null, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindBySupplierProductsAndDateBetweenSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String after = LocalDate.now().minusDays(1).toString();
        String before = LocalDate.now().plusDays(1).toString();

        when(productRepository.findBySupplierIgnoreCaseContainingAndCreateDateBetween(any(String.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findBySupplier(EntityMock.SUPPLIER, after, before, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindBySupplierProductsAndDateAfterSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String after = LocalDate.now().minusDays(1).toString();

        when(productRepository.findBySupplierIgnoreCaseContainingAndCreateDateAfter(any(String.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findBySupplier(EntityMock.SUPPLIER, after, null, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindBySupplierProductsAndDateBeforeSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String before = LocalDate.now().plusDays(1).toString();

        when(productRepository.findBySupplierIgnoreCaseContainingAndCreateDateBefore(any(String.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findBySupplier(EntityMock.SUPPLIER, null, before, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindBySupplierEntityNotFoundException() {
        Page<ProductDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(productRepository.findBySupplierIgnoreCaseContaining(any(String.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findBySupplier(EntityMock.PRODUCT_TITLE, null, null, page.getPageable()));
    }

    @Test
    void testFindByCategory() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(productRepository.findByCategoriesName(any(String.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findByCategory(EntityMock.CATEGORY, null, null, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindByCategoryProductsAndDateBetweenSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String after = LocalDate.now().minusDays(1).toString();
        String before = LocalDate.now().plusDays(1).toString();

        when(productRepository.findByCategoriesNameAndCreateDateBetween(any(String.class), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findByCategory(EntityMock.CATEGORY, after, before, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindByCategoryProductsAndDateAfterSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String after = LocalDate.now().minusDays(1).toString();

        when(productRepository.findByCategoriesNameAndCreateDateAfter(any(String.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findByCategory(EntityMock.CATEGORY, after, null, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindByCategoryProductsAndDateBeforeSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String before = LocalDate.now().plusDays(1).toString();

        when(productRepository.findByCategoriesNameAndCreateDateBefore(any(String.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findByCategory(EntityMock.CATEGORY, null, before, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindByCategoryNotFound() {
        Page<ProductDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(productRepository.findByCategoriesName(any(String.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findByCategory(EntityMock.CATEGORY, null, null, page.getPageable()));
    }

    @Test
    void testFindByCategoryInvalidCategoryException() {
        Page<ProductDtoPagination> page = EntityMock.pageEmpty();

        when(productRepository.findByCategoriesName(any(String.class), any(Pageable.class))).thenThrow(IllegalArgumentException.class);

        InvalidCategoryException exception = assertThrows(InvalidCategoryException.class, () -> {
            productService.findByCategory("MOUSE", null, null, page.getPageable());
        });

        assertEquals("Category does not exist", exception.getMessage());
    }

    @Test
    void testFindBySkuSuccess() {
        Product product = EntityMock.product();
        ProductResponseDto responseDto = EntityMock.productResponseDto();

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));
        when(productMapper.toDto(any(Product.class))).thenReturn(responseDto);

        ProductResponseDto response = productService.findBySku(EntityMock.SKU);

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindBySkuEntityNotFoundException() {
        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.findBySku(EntityMock.SKU));
    }

    @Test
    void testFindAllActivedSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(productRepository.findAllByStatus(eq(true), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findAllActived(null, null, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindAllActivedAndDateBetweenSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String after = LocalDate.now().minusDays(1).toString();
        String before = LocalDate.now().plusDays(1).toString();

        when(productRepository.findAllByStatusAndCreateDateBetween(anyBoolean(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findAllActived(after, before, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindAllActivedAndDateAfterSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String after = LocalDate.now().minusDays(1).toString();

        when(productRepository.findAllByStatusAndCreateDateAfter(anyBoolean(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findAllActived(after, null, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindAllActivedAndDateBeforeSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String before = LocalDate.now().plusDays(1).toString();

        when(productRepository.findAllByStatusAndCreateDateBefore(anyBoolean(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findAllActived(null, before, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindAllActivedEntityNotFoundException() {
        Page<ProductDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(productRepository.findAllByStatus(eq(true), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findAllActived(null, null, page.getPageable()));
    }

    @Test
    void testFindAllDisabledSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();

        when(productRepository.findAllByStatus(eq(false), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findAllDisabled(null, null, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindAllDisabledAndDateBetweenSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String after = LocalDate.now().minusDays(1).toString();
        String before = LocalDate.now().plusDays(1).toString();

        when(productRepository.findAllByStatusAndCreateDateBetween(anyBoolean(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findAllDisabled(after, before, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindAllDisabledAndDateAfterSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String after = LocalDate.now().minusDays(1).toString();

        when(productRepository.findAllByStatusAndCreateDateAfter(anyBoolean(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findAllDisabled(after, null, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindAllDisabledAndDateBeforeSuccess() {
        Page<ProductDtoPagination> page = EntityMock.page();
        PageableDto pageableDto = EntityMock.pageableDto();
        String before = LocalDate.now().plusDays(1).toString();

        when(productRepository.findAllByStatusAndCreateDateBefore(anyBoolean(), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findAllDisabled(null, before, page.getPageable());

        verifyResult(response, EntityMock.productDtoPaginationMock());
    }

    @Test
    void testFindAllDisabledEntityNotFoundException() {
        Page<ProductDtoPagination> page = EntityMock.pageEmpty();
        PageableDto pageableDto = EntityMock.pageableDtoEmpty();

        when(productRepository.findAllByStatus(eq(false), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findAllDisabled(null, null, page.getPageable()));
    }

    @Test
    void testUpdateBySkuSuccess() {
        Product product = EntityMock.product();
        ProductUpdateDto updateDto = EntityMock.productUpdateDto();

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));
        when(productMapper.toDto(any(Product.class))).thenReturn(EntityMock.productResponseDto());

        productService.update(EntityMock.SKU, updateDto, null, null, null);
    }

    @Test
    void testUpdateBySkuEntityNotFoundException() {
        ProductUpdateDto updateDto = EntityMock.productUpdateDto();

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.update(EntityMock.SKU, updateDto, null, null, null));
    }

    @Test
    void testAlterStatusBySkuSuccess() {
        Product product = EntityMock.product();
        ProductResponseDto responseDto = EntityMock.productResponseDto();
        responseDto.setStatus(false);

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));
        when(productMapper.toDto(any(Product.class))).thenReturn(responseDto);

        ProductResponseDto response = productService.update(EntityMock.SKU, null, Boolean.FALSE, null, null);

        assertEquals(response.getStatus(), Boolean.FALSE);
    }

    @Test
    void testAlterStatusBySkuInvalidProductStatusException() {
        Product product = EntityMock.product();
        product.setQuantity(0);

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));

        assertThrows(InvalidProductStatusException.class, () -> productService.update(EntityMock.SKU, null, Boolean.FALSE, null, null));
    }

    @Test
    void testAddProductBySkuSuccess() {
        Product product = EntityMock.product();
        ProductResponseDto responseDto = EntityMock.productResponseDto();
        responseDto.setQuantity(responseDto.getQuantity() + 1);

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));
        when(productMapper.toDto(any(Product.class))).thenReturn(responseDto);

        ProductResponseDto response = productService.update(EntityMock.SKU, null, null, 1, null);

        assertNotNull(response);
        assertEquals(EntityMock.QUANTITY + 1, response.getQuantity());
    }


    @Test
    void testAddProductBySkuEntityNotFoundException() {
        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.update(EntityMock.SKU, null, null, 1, null));
    }

    @Test
    void testAddProductBySkuInvalidQuantityException() {
        Product product = EntityMock.product();

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));

        assertThrows(InvalidQuantityException.class, () -> productService.update(EntityMock.SKU, null, null, 0, null));
    }

    @Test
    void testSubProductBySkuSuccess() {
        Product product = EntityMock.product();
        ProductResponseDto responseDto = EntityMock.productResponseDto();
        responseDto.setQuantity(responseDto.getQuantity() - 1);

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));
        when(productMapper.toDto(any(Product.class))).thenReturn(responseDto);

        ProductResponseDto response = productService.update(EntityMock.SKU, null, null, null, 1);

        assertNotNull(response);
        assertEquals(EntityMock.QUANTITY - 1, response.getQuantity());
    }

    @Test
    void testSubProductBySkuEntityNotFoundException() {
        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.update(EntityMock.SKU, null, null, null, 1));
    }

    @Test
    void testSubProductBySkuInvalidQuantityException() {
        Product product = EntityMock.product();

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));

        assertThrows(InvalidQuantityException.class, () -> productService.update(EntityMock.SKU, null, null, null, 0));
    }

    @Test
    void testSubProductBySkuInvalidQuantityAvailableException() {
        Product product = EntityMock.product();

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));

        assertThrows(InvalidQuantityException.class, () -> productService.update(EntityMock.SKU, null, null, null, 11));
    }

    @Test
    void testDeleteProductBySkuSuccess() {
        when(productRepository.existsBySku(any(String.class))).thenReturn(true);

        productService.deleteBySku(EntityMock.SKU);
    }

    @Test
    void testDeleteProductBySkuEntityNotFoundException() {
        when(productRepository.existsBySku(any(String.class))).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> productService.deleteBySku(EntityMock.SKU));
    }
}