package com.esand.products;

import com.esand.products.entity.Category;
import com.esand.products.entity.EntityMock;
import com.esand.products.entity.Product;
import com.esand.products.repository.CategoryRepository;
import com.esand.products.repository.ProductRepository;
import com.esand.products.web.dto.ProductCreateDto;
import com.esand.products.web.dto.ProductUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;


import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductsIntegrationTests {

	@Autowired
	private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	void createCategory() {
		categoryRepository.save(EntityMock.category());
	}

	void createProduct() {
		Category category = categoryRepository.save(EntityMock.category());
		Product product = EntityMock.product();
		product.setCategories(List.of(category));
		productRepository.save(product);
	}

	void manualCreateProduct(Category category, Product product) {
		Category categorySaved = categoryRepository.save(EntityMock.category());
		product.setCategories(List.of(categorySaved));
		productRepository.save(product);
	}

	@AfterEach
	void deleteAll() {
		productRepository.deleteAll();
		categoryRepository.deleteAll();
	}

	@Test
	void testCreateProductSuccess() throws Exception {
		createCategory();
		ProductCreateDto createDto = EntityMock.createDto();

		String productJson = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.title").value(createDto.getTitle()))
				.andExpect(jsonPath("$.sku").value(createDto.getSku()))
				.andExpect(jsonPath("$.price").value(createDto.getPrice()))
				.andExpect(jsonPath("$.quantity").value(createDto.getQuantity()))
				.andExpect(jsonPath("$.categories[0].name").value(createDto.getCategory()));
	}

	@Test
	void testCreateProductInvalidDataException() throws Exception {
		ProductCreateDto createDto = EntityMock.createDto();
		createDto.setCategory("MOUSESES");

		String productJson = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productJson))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Category not found"));
	}

	@Test
	void testCreateProductTitleUniqueViolationException() throws Exception {
		createProduct();
		ProductCreateDto createDto = EntityMock.createDto();

		String productJson = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productJson))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.message").value("There is already a product registered with this title"));
	}

	@Test
	void testCreateProductSkuUniqueViolationException() throws Exception {
		createProduct();
		ProductCreateDto createDto = EntityMock.createDto();
		createDto.setTitle("Any Title");

		String productJson = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productJson))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.message").value("There is already a product registered with this sku"));
	}

	@Test
	void testFindAllProductsSuccess() throws Exception {
		createProduct();

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindAllProductsEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No products found"));
	}

	@Test
	void testFindProductsByTitleSuccess() throws Exception {
		createProduct();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/title/Wireless MouseS")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindProductsByTitleEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/title/Wireless MouseS")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No products found by title"));
	}

	@Test
	void testFindProductsBySupplierSuccess() throws Exception {
		createProduct();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/supplier/Mach")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindProductsBySupplierEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/supplier/Mach")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No products found by supplier"));
	}

	@Test
	void testFindProductsByCategorySuccess() throws Exception {
		createProduct();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/category/mouses")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindProductsByCategoryInvalidCategoryException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/category/asdasdasd")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No products found by category"));
	}

	@Test
	void testFindProductsByCategoryEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/category/mouses")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No products found by category"));
	}

	@Test
	void testFindProductsBySkuSuccess() throws Exception {
		createProduct();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/sku/" + EntityMock.SKU)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindProductsBySkuEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/sku/" + EntityMock.SKU)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Product not found by sku"));
	}

	@Test
	void testFindAllActivedSuccess() throws Exception {
		createProduct();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/actived")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindAllActivedEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/actived")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No active product found"));
	}

	@Test
	void testFindAllDisabledSuccess() throws Exception {
		testToggleStatusBySkuSuccess();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/disabled")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindAllDisabledEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/disabled")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No disabled product found"));
	}

	@Test
	void testFindByDateBetweenSuccess() throws Exception {
		createProduct();
		String after = LocalDate.now().minusDays(1).toString();
		String before = LocalDate.now().plusDays(1).toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/date?afterDate=" + after + "&beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindByDateAfterSuccess() throws Exception {
		createProduct();
		String after = LocalDate.now().minusDays(1).toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/date?afterDate=" + after)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindByDateBeforeSuccess() throws Exception {
		createProduct();
		String before = LocalDate.now().plusDays(1).toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/date?beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindByDateNoDateParametersProvided() throws Exception {
		createProduct();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/date?")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No date parameters provided"));
	}

	@Test
	void testFindByDateEntityNotFoundException() throws Exception {
		String after = LocalDate.now().minusDays(1).toString();
		String before = LocalDate.now().plusDays(1).toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/date?afterDate=" + after + "&beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No products found by date(s)"));
	}

	@Test
	void testEditProductDataBySkuSuccess() throws Exception {
		createProduct();
		ProductUpdateDto updateDto = EntityMock.productUpdateDto();

		String updateDtoJson = objectMapper.writeValueAsString(updateDto);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/edit/" + EntityMock.SKU)
						.contentType(MediaType.APPLICATION_JSON)
						.content(updateDtoJson))
				.andExpect(status().isNoContent());
	}

	@Test
	void testEditProductDataBySkuInvalidDataException() throws Exception {
		createProduct();
		ProductUpdateDto updateDto = new ProductUpdateDto(
				"",
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

		String updateDtoJson = objectMapper.writeValueAsString(updateDto);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/edit/" + EntityMock.SKU)
						.contentType(MediaType.APPLICATION_JSON)
						.content(updateDtoJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid request content."))
				.andExpect(jsonPath("$.errors.title").value("size must be between 5 and 200"));
	}

	@Test
	void testEditProductDataBySkuEntityNotFoundException() throws Exception {
		ProductUpdateDto updateDto = EntityMock.productUpdateDto();

		String updateDtoJson = objectMapper.writeValueAsString(updateDto);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/edit/" + EntityMock.SKU)
						.contentType(MediaType.APPLICATION_JSON)
						.content(updateDtoJson))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Product not found by sku"));
	}

	@Test
	void testToggleStatusBySkuSuccess() throws Exception {
		createProduct();
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/status/" + EntityMock.SKU)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value("updated status for: false"));
	}

	@Test
	void testToggleStatusBySkuInvalidProductStatusException() throws Exception {
		Product product = EntityMock.product();
		product.setQuantity(0);
		manualCreateProduct(EntityMock.category(), product);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/status/" + EntityMock.SKU)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Cannot alter status when quantity is 0"));
	}

	@Test
	void testToggleStatusBySkuEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/status/" + EntityMock.SKU)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Product not found by sku"));
	}

	@Test
	void testAddProductBySkuSuccess() throws Exception {
		createProduct();
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/sku/" + EntityMock.SKU + "/add/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value("updated quantity for: 11"));
	}

	@Test
	void testAddProductBySkuInvalidQuantityException() throws Exception {
		createProduct();
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/sku/" + EntityMock.SKU + "/add/0")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("No quantity stated"));
	}

	@Test
	void testAddProductBySkuEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/sku/" + EntityMock.SKU + "/add/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Product not found by sku"));
	}

	@Test
	void testSubProductBySkuSuccess() throws Exception {
		createProduct();
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/sku/" + EntityMock.SKU + "/sub/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value("updated quantity for: 9"));
	}

	@Test
	void testSubProductBySkuInvalidQuantityException() throws Exception {
		createProduct();
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/sku/" + EntityMock.SKU + "/sub/0")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("No quantity stated"));
	}

	@Test
	void testSubProductBySkuInvalidQuantityAvailableException() throws Exception {
		createProduct();
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/sku/" + EntityMock.SKU + "/sub/11")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("The quantity of available products is 10"));
	}

	@Test
	void testSubProductBySkuEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/sku/" + EntityMock.SKU + "/sub/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Product not found by sku"));
	}

	@Test
	void testDeleteProductBySkuSuccess() throws Exception {
		createProduct();
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/delete/" + EntityMock.SKU)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}

	@Test
	void testDeleteProductBySkuEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/delete/" + EntityMock.SKU)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Product not found by sku"));
	}
}
