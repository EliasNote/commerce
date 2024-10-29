//package com.esand.products.service;
//
//import com.esand.products.entity.Category;
//import com.esand.products.entity.EntityMock;
//import com.esand.products.entity.Product;
//import com.esand.products.exception.*;
//import com.esand.products.repository.CategoryRepository;
//import com.esand.products.repository.ProductRepository;
//import com.esand.products.repository.pagination.ProductDtoPagination;
//import com.esand.products.web.dto.PageableDto;
//import com.esand.products.web.dto.ProductCreateDto;
//import com.esand.products.web.dto.ProductResponseDto;
//import com.esand.products.web.dto.ProductUpdateDto;
//import com.esand.products.web.mapper.ProductMapper;
//import org.junit.Ignore;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//
//class ProductServiceTest {
//
//    @Mock
//    private ProductRepository productRepository;
//
//    @Mock
//    private ProductMapper productMapper;
//
//    @Mock
//    private CategoryService categoryService;
//
//    @InjectMocks
//    private ProductService productService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testSaveProductSuccess() {
//        ProductCreateDto createDto = EntityMock.createDto();
//        Product product = EntityMock.product();
//        ProductResponseDto responseDto = EntityMock.productResponseDto();
//        Category category = EntityMock.category();
//
//        when(productMapper.toProduct(any(ProductCreateDto.class))).thenReturn(product);
//        when(productRepository.save(any(Product.class))).thenReturn(product);
//        when(productMapper.toDto(any(Product.class))).thenReturn(responseDto);
//        when(categoryService.findByName(any(String.class))).thenReturn(category);
//
//        ProductResponseDto response = productService.save(createDto);
//
//        assertNotNull(response);
//        assertEquals(EntityMock.PRODUCT_TITLE, response.getTitle());
//        assertEquals(EntityMock.PRODUCT_DESCRIPTION, response.getDescription());
//        assertEquals(EntityMock.PRICE, response.getPrice());
//        assertEquals(EntityMock.CATEGORY, response.getCategories().getFirst().getName());
//        assertEquals(EntityMock.QUANTITY, response.getQuantity());
//        assertEquals(EntityMock.SKU, response.getSku());
//        assertTrue(response.getStatus());
//    }
//
//    @Test
//    void testSaveProductTitleUniqueViolationException() {
//        ProductCreateDto createDto = EntityMock.createDto();
//
//        when(productRepository.existsByTitle(any(String.class))).thenReturn(true);
//
//        assertThrows(TitleUniqueViolationException.class, () -> productService.save(createDto));
//    }
//
//    @Test
//    void testSaveProductSkuUniqueViolationException() {
//        ProductCreateDto createDto = EntityMock.createDto();
//
//        when(productRepository.existsBySku(any(String.class))).thenReturn(true);
//
//        assertThrows(SkuUniqueViolationException.class, () -> productService.save(createDto));
//    }
//
//    @Test
//    void testSaveProductEntityNotFoundException() {
//        ProductCreateDto createDto = EntityMock.createDto();
//
//        when(categoryService.findByName(any(String.class))).thenThrow(EntityNotFoundException.class);
//
//        assertThrows(EntityNotFoundException.class, () -> productService.save(createDto));
//    }
//
//    @Test
//    void testFindAllSuccess() {
//        Page<ProductDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(productRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
//        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = productService.findAll(null, null, page.getPageable());
//
//        assertNotNull(response);
//        assertNotNull(response.getContent());
//        assertNotNull(response.getContent().get(0));
//        assertEquals(1, response.getContent().size());
//    }
//
//    @Test
//    void testFindAllProductsEntityNotFoundException() {
//        Page<ProductDtoPagination> page = EntityMock.pageEmpty();
//        PageableDto pageableDto = EntityMock.pageableDtoEmpty();
//
//        when(productRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
//        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        assertThrows(EntityNotFoundException.class, () -> productService.findAll(null, null, page.getPageable()));
//    }
//
//    @Test
//    void testFindByTitleSuccess() {
//        Page<ProductDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(productRepository.findByTitleIgnoreCaseContaining(any(Pageable.class), any(String.class))).thenReturn(page);
//        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = productService.findByTitle(page.getPageable(), EntityMock.PRODUCT_TITLE);
//
//        assertNotNull(response);
//        assertNotNull(response.getContent());
//        assertNotNull(response.getContent().get(0));
//        assertEquals(1, response.getContent().size());
//    }
//
//    @Test
//    void testFindByTitleEntityNotFoundException() {
//        Page<ProductDtoPagination> page = EntityMock.pageEmpty();
//        PageableDto pageableDto = EntityMock.pageableDtoEmpty();
//
//        when(productRepository.findByTitleIgnoreCaseContaining(any(Pageable.class), any(String.class))).thenReturn(page);
//        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        assertThrows(EntityNotFoundException.class, () -> productService.findByTitle(page.getPageable(), EntityMock.PRODUCT_TITLE));
//    }
//
//    @Test
//    void testFindBySupplierSuccess() {
//        Page<ProductDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(productRepository.findBySupplierIgnoreCaseContaining(any(Pageable.class), any(String.class))).thenReturn(page);
//        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = productService.findBySupplier(page.getPageable(), EntityMock.PRODUCT_TITLE);
//
//        assertNotNull(response);
//        assertNotNull(response.getContent());
//        assertNotNull(response.getContent().get(0));
//        assertEquals(1, response.getContent().size());
//    }
//
//    @Test
//    void testFindBySupplierEntityNotFoundException() {
//        Page<ProductDtoPagination> page = EntityMock.pageEmpty();
//        PageableDto pageableDto = EntityMock.pageableDtoEmpty();
//
//        when(productRepository.findBySupplierIgnoreCaseContaining(any(Pageable.class), any(String.class))).thenReturn(page);
//        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        assertThrows(EntityNotFoundException.class, () -> productService.findBySupplier(page.getPageable(), EntityMock.PRODUCT_TITLE));
//    }
//
//    @Test
//    void testFindByCategory() {
//        Page<ProductDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(productRepository.findByCategoriesName(any(Pageable.class), any(String.class))).thenReturn(page);
//        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = productService.findByCategory(page.getPageable(), EntityMock.CATEGORY);
//
//        assertNotNull(response);
//        assertNotNull(response.getContent());
//        assertNotNull(response.getContent().get(0));
//        assertEquals(1, response.getContent().size());
//    }
//
//    @Test
//    void testFindByCategoryNotFound() {
//        Page<ProductDtoPagination> page = EntityMock.pageEmpty();
//        PageableDto pageableDto = EntityMock.pageableDtoEmpty();
//
//        when(productRepository.findByCategoriesName(any(Pageable.class), any(String.class))).thenReturn(page);
//        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        assertThrows(EntityNotFoundException.class, () -> productService.findByCategory(page.getPageable(), EntityMock.CATEGORY));
//    }
//
//    @Test
//    void testFindByCategoryInvalidCategoryException() {
//        Pageable pageable = PageRequest.of(0, 10);
//
//        when(productRepository.findByCategoriesName(any(Pageable.class), any(String.class))).thenThrow(IllegalArgumentException.class);
//
//        InvalidCategoryException exception = assertThrows(InvalidCategoryException.class, () -> {
//            productService.findByCategory(pageable, "MOUSE");
//        });
//
//        assertEquals("Category does not exist", exception.getMessage());
//    }
//
//    @Test
//    void testFindBySkuSuccess() {
//        Product product = EntityMock.product();
//        ProductResponseDto responseDto = EntityMock.productResponseDto();
//
//        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));
//        when(productMapper.toDto(any(Product.class))).thenReturn(responseDto);
//
//        ProductResponseDto response = productService.findBySku(EntityMock.SKU);
//
//        assertNotNull(response);
//        assertEquals(EntityMock.PRODUCT_TITLE, response.getTitle());
//        assertEquals(EntityMock.PRODUCT_DESCRIPTION, response.getDescription());
//        assertEquals(EntityMock.PRICE, response.getPrice());
//        assertEquals(EntityMock.CATEGORY, response.getCategories().get(0).getName());
//        assertEquals(EntityMock.QUANTITY, response.getQuantity());
//        assertEquals(EntityMock.SKU, response.getSku());
//        assertTrue(response.getStatus());
//    }
//
//    @Test
//    void testFindBySkuEntityNotFoundException() {
//        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> productService.findBySku(EntityMock.SKU));
//    }
//
//    @Test
//    void testFindAllActivedSuccess() {
//        Page<ProductDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(productRepository.findAllByStatus(any(Pageable.class), eq(true))).thenReturn(page);
//        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = productService.findAllActived(page.getPageable());
//
//        assertNotNull(response);
//        assertNotNull(response.getContent());
//        assertNotNull(response.getContent().get(0));
//        assertEquals(1, response.getContent().size());
//    }
//
//    @Test
//    void testFindAllActivedEntityNotFoundException() {
//        Page<ProductDtoPagination> page = EntityMock.pageEmpty();
//        PageableDto pageableDto = EntityMock.pageableDtoEmpty();
//
//        when(productRepository.findAllByStatus(any(Pageable.class), eq(true))).thenReturn(page);
//        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        assertThrows(EntityNotFoundException.class, () -> productService.findAllActived(page.getPageable()));
//    }
//
//    @Test
//    void testFindAllDisabledSuccess() {
//        Page<ProductDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(productRepository.findAllByStatus(any(Pageable.class), eq(false))).thenReturn(page);
//        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = productService.findAllDisabled(page.getPageable());
//
//        assertNotNull(response);
//        assertNotNull(response.getContent());
//        assertNotNull(response.getContent().get(0));
//        assertEquals(1, response.getContent().size());
//    }
//
//    @Test
//    void testFindAllDisabledEntityNotFoundException() {
//        Page<ProductDtoPagination> page = EntityMock.pageEmpty();
//        PageableDto pageableDto = EntityMock.pageableDtoEmpty();
//
//        when(productRepository.findAllByStatus(any(Pageable.class), eq(false))).thenReturn(page);
//        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        assertThrows(EntityNotFoundException.class, () -> productService.findAllDisabled(page.getPageable()));
//    }
//
//    @Test
//    void testUpdateBySkuSuccess() {
//        Product product = EntityMock.product();
//        ProductUpdateDto updateDto = EntityMock.productUpdateDto();
//
//        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));
//        doNothing().when(productMapper).updateProduct(any(ProductUpdateDto.class), any(Product.class));
//
//        productService.update(EntityMock.SKU, updateDto);
//    }
//
//    @Test
//    void testUpdateBySkuEntityNotFoundException() {
//        ProductUpdateDto updateDto = EntityMock.productUpdateDto();
//
//        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> productService.update(EntityMock.SKU, updateDto));
//    }
//
//    @Test
//    void testAlterStatusBySkuSuccess() {
//        Product product = EntityMock.product();
//
//        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));
//
//        String response = productService.alter(EntityMock.SKU);
//
//        assertNotNull(response);
//        assertEquals("false", response);
//    }
//
//    @Test
//    void testAlterStatusBySkuInvalidProductStatusException() {
//        Product product = EntityMock.product();
//        product.setQuantity(0);
//
//        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));
//
//        assertThrows(InvalidProductStatusException.class, () -> productService.alter(EntityMock.SKU));
//    }
//
//    @Test
//    void testAddProductBySkuSuccess() {
//        Product product = EntityMock.product();
//
//        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));
//
//        String response = productService.add(EntityMock.SKU, 1);
//
//        assertNotNull(response);
//        assertEquals("11", response);
//    }
//
//    @Test
//    void testAddProductBySkuEntityNotFoundException() {
//        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> productService.add(EntityMock.SKU, 1));
//    }
//
//    @Test
//    void testAddProductBySkuInvalidQuantityException() {
//        Product product = EntityMock.product();
//
//        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));
//
//        assertThrows(InvalidQuantityException.class, () -> productService.add(EntityMock.SKU, 0));
//    }
//
//    @Test
//    void testSubProductBySkuSuccess() {
//        Product product = EntityMock.product();
//
//        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));
//
//        String response = productService.sub(EntityMock.SKU, 1);
//
//        assertNotNull(response);
//        assertEquals("9", response);
//    }
//
//    @Test
//    void testSubProductBySkuEntityNotFoundException() {
//        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> productService.sub(EntityMock.SKU, 1));
//    }
//
//    @Test
//    void testSubProductBySkuInvalidQuantityException() {
//        Product product = EntityMock.product();
//
//        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));
//
//        assertThrows(InvalidQuantityException.class, () -> productService.sub(EntityMock.SKU, 0));
//    }
//
//    @Test
//    void testSubProductBySkuInvalidQuantityAvailableException() {
//        Product product = EntityMock.product();
//
//        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));
//
//        assertThrows(InvalidQuantityException.class, () -> productService.sub(EntityMock.SKU, 11));
//    }
//
//    @Test
//    void testDeleteProductBySkuSuccess() {
//        when(productRepository.existsBySku(any(String.class))).thenReturn(true);
//
//        productService.deleteBySku(EntityMock.SKU);
//    }
//
//    @Test
//    void testDeleteProductBySkuEntityNotFoundException() {
//        when(productRepository.existsBySku(any(String.class))).thenReturn(false);
//
//        assertThrows(EntityNotFoundException.class, () -> productService.deleteBySku(EntityMock.SKU));
//    }
//}