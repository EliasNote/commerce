package com.esand.products.service;

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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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
        ProductCreateDto createDto = new ProductCreateDto(
                "Wireless MouseS",
                "A high precision wireless m",
                29.99,
                "MOUSES",
                10,
                "MOUSE-2024-WL-0010",
                0.1,
                10.0,
                5.0,
                3.0,
                "Mach Supplies Inc."
        );
        Product product = new Product(
                1L,
                "Wireless MouseS",
                "A high precision wireless m",
                29.99, Product.Category.MOUSES,
                10,
                "MOUSE-2024-WL-0010",
                0.1,
                10.0,
                5.0,
                3.0,
                "Mach Supplies Inc.",
                LocalDateTime.now(),
                true
        );

        ProductResponseDto responseDto = new ProductResponseDto(
                "Wireless MouseS",
                "A high precision wireless m",
                29.99,
                "MOUSES",
                10,
                "MOUSE-2024-WL-0010",
                true
        );

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
        ProductCreateDto createDto = new ProductCreateDto(
                "Wireless MouseS",
                "A high precision wireless m",
                29.99,
                "MOUSES",
                10,
                "MOUSE-2024-WL-0010",
                0.1,
                10.0,
                5.0,
                3.0,
                "Mach Supplies Inc."
        );

        when(productRepository.existsByTitle(any(String.class))).thenReturn(true);

        assertThrows(TitleUniqueViolationException.class, () -> productService.save(createDto));
    }

    @Test
    void testSaveProductSkuUniqueViolationException() {
        ProductCreateDto createDto = new ProductCreateDto(
                "Wireless MouseS",
                "A high precision wireless m",
                29.99,
                "MOUSES",
                10,
                "MOUSE-2024-WL-0010",
                0.1,
                10.0,
                5.0,
                3.0,
                "Mach Supplies Inc."
        );

        when(productRepository.existsBySku(any(String.class))).thenReturn(true);

        assertThrows(SkuUniqueViolationException.class, () -> productService.save(createDto));
    }

    @Test
    void testFindAllSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDtoPagination> content = List.of(
                new ProductDtoPagination() {
                    public String getTitle() { return "Wireless MouseS"; }
                    public String getDescription() { return "A high precision wireless m"; }
                    public Double getPrice() { return 29.99; }
                    public String getCategory() { return "MOUSES"; }
                    public Integer getQuantity() { return 10; }
                    public String getSku() { return "MOUSE-2024-WL-0010"; }
                    public Boolean getStatus() { return true; }
                }
        );

        Page<ProductDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(productRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findAll(pageable);

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindAllProductsEntityNotFoundException() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDtoPagination> content = List.of();

        Page<ProductDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(productRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findAll(pageable));
    }

    @Test
    void testFindByTitleSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDtoPagination> content = List.of(
                new ProductDtoPagination() {
                    public String getTitle() { return "Wireless MouseS"; }
                    public String getDescription() { return "A high precision wireless m"; }
                    public Double getPrice() { return 29.99; }
                    public String getCategory() { return "MOUSES"; }
                    public Integer getQuantity() { return 10; }
                    public String getSku() { return "MOUSE-2024-WL-0010"; }
                    public Boolean getStatus() { return true; }
                }
        );

        Page<ProductDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(productRepository.findByTitleIgnoreCaseContaining(any(Pageable.class), any(String.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findByTitle(pageable, "Wireless MouseS");

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindByTitleEntityNotFoundException() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDtoPagination> content = List.of();

        Page<ProductDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(productRepository.findByTitleIgnoreCaseContaining(any(Pageable.class), any(String.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findByTitle(pageable, "Wireless MouseS"));
    }

    @Test
    void testFindBySupplierSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDtoPagination> content = List.of(
                new ProductDtoPagination() {
                    public String getTitle() { return "Wireless MouseS"; }
                    public String getDescription() { return "A high precision wireless m"; }
                    public Double getPrice() { return 29.99; }
                    public String getCategory() { return "MOUSES"; }
                    public Integer getQuantity() { return 10; }
                    public String getSku() { return "MOUSE-2024-WL-0010"; }
                    public Boolean getStatus() { return true; }
                }
        );

        Page<ProductDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(productRepository.findBySupplierIgnoreCaseContaining(any(Pageable.class), any(String.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findBySupplier(pageable, "Wireless MouseS");

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindBySupplierEntityNotFoundException() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDtoPagination> content = List.of();

        Page<ProductDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(productRepository.findBySupplierIgnoreCaseContaining(any(Pageable.class), any(String.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findBySupplier(pageable, "Wireless MouseS"));
    }

    @Test
    void testFindByCategory() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDtoPagination> content = List.of(
                new ProductDtoPagination() {
                    public String getTitle() { return "Wireless MouseS"; }
                    public String getDescription() { return "A high precision wireless m"; }
                    public Double getPrice() { return 29.99; }
                    public String getCategory() { return "MOUSES"; }
                    public Integer getQuantity() { return 10; }
                    public String getSku() { return "MOUSE-2024-WL-0010"; }
                    public Boolean getStatus() { return true; }
                }
        );

        Page<ProductDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(productRepository.findByCategory(any(Pageable.class), any(Product.Category.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findByCategory(pageable, "MOUSES");

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindByCategoryNotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDtoPagination> content = List.of();

        Page<ProductDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(productRepository.findByCategory(any(Pageable.class), any(Product.Category.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findByCategory(pageable, "MOUSES"));
    }

    @Test
    void testFindByCategoryInvalidCategoryException() {
        Pageable pageable = PageRequest.of(0, 10);

        when(productRepository.findByCategory(any(Pageable.class), any(Product.Category.class))).thenThrow(InvalidCategoryException.class);

        assertThrows(InvalidCategoryException.class, () -> productService.findByCategory(pageable, "MOUSE"));
    }

    @Test
    void testFindBySkuSuccess() {
        Product product = new Product(
                1L, "Wireless MouseS",
                "A high precision wireless m",
                29.99, Product.Category.MOUSES,
                10, "MOUSE-2024-WL-0010",
                0.1,
                10.0,
                5.0,
                3.0,
                "Mach Supplies Inc.",
                LocalDateTime.now(),
                true
        );
        ProductResponseDto responseDto = new ProductResponseDto(
                "Wireless MouseS",
                "A high precision wireless m",
                29.99,
                "MOUSES",
                10,
                "MOUSE-2024-WL-0010",
                true
        );

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
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDtoPagination> content = List.of(
                new ProductDtoPagination() {
                    public String getTitle() { return "Wireless MouseS"; }
                    public String getDescription() { return "A high precision wireless m"; }
                    public Double getPrice() { return 29.99; }
                    public String getCategory() { return "MOUSES"; }
                    public Integer getQuantity() { return 10; }
                    public String getSku() { return "MOUSE-2024-WL-0010"; }
                    public Boolean getStatus() { return true; }
                }
        );

        Page<ProductDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(productRepository.findAllByStatus(any(Pageable.class), eq(true))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findAllActived(pageable);

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindAllActivedEntityNotFoundException() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDtoPagination> content = List.of();

        Page<ProductDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(productRepository.findAllByStatus(any(Pageable.class), eq(true))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findAllActived(pageable));
    }

    @Test
    void testFindAllDisabledSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDtoPagination> content = List.of(
                new ProductDtoPagination() {
                    public String getTitle() { return "Wireless MouseS"; }
                    public String getDescription() { return "A high precision wireless m"; }
                    public Double getPrice() { return 29.99; }
                    public String getCategory() { return "MOUSES"; }
                    public Integer getQuantity() { return 10; }
                    public String getSku() { return "MOUSE-2024-WL-0010"; }
                    public Boolean getStatus() { return true; }
                }
        );

        Page<ProductDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(productRepository.findAllByStatus(any(Pageable.class), eq(false))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findAllDisabled(pageable);

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindAllDisabledEntityNotFoundException() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDtoPagination> content = List.of();

        Page<ProductDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(productRepository.findAllByStatus(any(Pageable.class), eq(false))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findAllDisabled(pageable));
    }

    @Test
    void testFindProductsByDateBetweenSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDtoPagination> content = List.of(
                new ProductDtoPagination() {
                    public String getTitle() { return "Wireless MouseS"; }
                    public String getDescription() { return "A high precision wireless m"; }
                    public Double getPrice() { return 29.99; }
                    public String getCategory() { return "MOUSES"; }
                    public Integer getQuantity() { return 10; }
                    public String getSku() { return "MOUSE-2024-WL-0010"; }
                    public Boolean getStatus() { return true; }
                }
        );

        Page<ProductDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(productRepository.findByCreateDateBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findProductsByDate(LocalDate.now().minusDays(1).toString(), LocalDate.now().plusDays(1).toString(), pageable);

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindProductsByDateAfterSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDtoPagination> content = List.of(
                new ProductDtoPagination() {
                    public String getTitle() { return "Wireless MouseS"; }
                    public String getDescription() { return "A high precision wireless m"; }
                    public Double getPrice() { return 29.99; }
                    public String getCategory() { return "MOUSES"; }
                    public Integer getQuantity() { return 10; }
                    public String getSku() { return "MOUSE-2024-WL-0010"; }
                    public Boolean getStatus() { return true; }
                }
        );

        Page<ProductDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(productRepository.findByCreateDateAfter(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findProductsByDate(LocalDate.now().minusDays(1).toString(), null, pageable);

        assertNotNull(response);
        assertNotNull(response.getContent());
        assertNotNull(response.getContent().get(0));
        assertEquals(1, response.getContent().size());
    }

    @Test
    void testFindProductsByDateBeforeSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDtoPagination> content = List.of(
                new ProductDtoPagination() {
                    public String getTitle() { return "Wireless MouseS"; }
                    public String getDescription() { return "A high precision wireless m"; }
                    public Double getPrice() { return 29.99; }
                    public String getCategory() { return "MOUSES"; }
                    public Integer getQuantity() { return 10; }
                    public String getSku() { return "MOUSE-2024-WL-0010"; }
                    public Boolean getStatus() { return true; }
                }
        );

        Page<ProductDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(productRepository.findByCreateDateBefore(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        PageableDto response = productService.findProductsByDate(null, LocalDate.now().plusDays(1).toString(), pageable);

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
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductDtoPagination> content = List.of();

        Page<ProductDtoPagination> page = new PageImpl<>(content, pageable, content.size());
        PageableDto pageableDto = new PageableDto();
        pageableDto.setContent(content);

        when(productRepository.findByCreateDateBefore(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
        when(productMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);

        assertThrows(EntityNotFoundException.class, () -> productService.findProductsByDate(null, LocalDate.now().plusDays(1).toString(), pageable));
    }

    @Test
    void testUpdateBySkuSuccess() {
        Product product = new Product(
                1L,
                "Wireless MouseS",
                "A high precision wireless m",
                29.99, Product.Category.MOUSES,
                10,
                "MOUSE-2024-WL-0010",
                0.1, 10.0,
                5.0,
                3.0,
                "Mach Supplies Inc.",
                LocalDateTime.now(),
                true
        );
        ProductUpdateDto updateDto = new ProductUpdateDto(
                "Updated Wireless MouseS",
                "An updated high precision wireless mouse",
                39.99,
                "MOUSES",
                15,
                "MOUSE-2024-WL-0010",
                0.2,
                12.0,
                6.0,
                4.0,
                "Updated Mach Supplies Inc."
        );

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));
        doNothing().when(productMapper).updateProduct(any(ProductUpdateDto.class), any(Product.class));

        productService.update("MOUSE-2024-WL-0010", updateDto);
    }

    @Test
    void testUpdateBySkuEntityNotFoundException() {
        ProductUpdateDto updateDto = new ProductUpdateDto(
                "Updated Wireless MouseS",
                "An updated high precision wireless mouse",
                39.99,
                "MOUSES",
                15,
                "MOUSE-2024-WL-0010",
                0.2,
                12.0,
                6.0,
                4.0,
                "Updated Mach Supplies Inc."
        );

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.update("MOUSE-2024-WL-0010", updateDto));
    }

    @Test
    void testAlterStatusBySkuSuccess() {
        Product product = new Product(
                1L,
                "Wireless MouseS",
                "A high precision wireless m",
                29.99, Product.Category.MOUSES,
                10,
                "MOUSE-2024-WL-0010",
                0.1, 10.0,
                5.0,
                3.0,
                "Mach Supplies Inc.",
                LocalDateTime.now(),
                true
        );

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));

        String response = productService.alter("MOUSE-2024-WL-0010");

        assertNotNull(response);
        assertEquals("false", response);
    }

    @Test
    void testAlterStatusBySkuInvalidProductStatusException() {
        Product product = new Product(
                1L,
                "Wireless MouseS",
                "A high precision wireless m",
                29.99, Product.Category.MOUSES,
                0,
                "MOUSE-2024-WL-0010",
                0.1, 10.0,
                5.0,
                3.0,
                "Mach Supplies Inc.",
                LocalDateTime.now(),
                true
        );

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));

        assertThrows(InvalidProductStatusException.class, () -> productService.alter("MOUSE-2024-WL-0010"));
    }

    @Test
    void testAddProductBySkuSuccess() {
        Product product = new Product(
                1L,
                "Wireless MouseS",
                "A high precision wireless m",
                29.99, Product.Category.MOUSES,
                10,
                "MOUSE-2024-WL-0010",
                0.1, 10.0,
                5.0,
                3.0,
                "Mach Supplies Inc.",
                LocalDateTime.now(),
                true
        );

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
        Product product = new Product(
                1L,
                "Wireless MouseS",
                "A high precision wireless m",
                29.99, Product.Category.MOUSES,
                10,
                "MOUSE-2024-WL-0010",
                0.1, 10.0,
                5.0,
                3.0,
                "Mach Supplies Inc.",
                LocalDateTime.now(),
                true
        );

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));

        assertThrows(InvalidQuantityException.class, () -> productService.add("MOUSE-2024-WL-0010", 0));
    }

    @Test
    void testSubProductBySkuSuccess() {
        Product product = new Product(
                1L,
                "Wireless MouseS",
                "A high precision wireless m",
                29.99, Product.Category.MOUSES,
                10,
                "MOUSE-2024-WL-0010",
                0.1, 10.0,
                5.0,
                3.0,
                "Mach Supplies Inc.",
                LocalDateTime.now(),
                true
        );

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
        Product product = new Product(
                1L,
                "Wireless MouseS",
                "A high precision wireless m",
                29.99, Product.Category.MOUSES,
                10,
                "MOUSE-2024-WL-0010",
                0.1, 10.0,
                5.0,
                3.0,
                "Mach Supplies Inc.",
                LocalDateTime.now(),
                true
        );

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));

        assertThrows(InvalidQuantityException.class, () -> productService.sub("MOUSE-2024-WL-0010", 0));
    }

    @Test
    void testSubProductBySkuInvalidQuantityAvailableException() {
        Product product = new Product(
                1L,
                "Wireless MouseS",
                "A high precision wireless m",
                29.99, Product.Category.MOUSES,
                10,
                "MOUSE-2024-WL-0010",
                0.1, 10.0,
                5.0,
                3.0,
                "Mach Supplies Inc.",
                LocalDateTime.now(),
                true
        );

        when(productRepository.findBySku(any(String.class))).thenReturn(Optional.of(product));

        assertThrows(InvalidQuantityException.class, () -> productService.sub("MOUSE-2024-WL-0010", 11));
    }
}