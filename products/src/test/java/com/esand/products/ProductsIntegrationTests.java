package com.esand.products;

import com.esand.products.entity.EntityMock;
import com.esand.products.repository.ProductRepository;
import com.esand.products.web.dto.ProductCreateDto;
import com.esand.products.web.dto.ProductUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setUp() {
		productRepository.deleteAll();
	}

	void createClient() {
		productRepository.save(EntityMock.product());
	}

	@Test
	void testCreateProductSuccess() throws Exception {
		ProductCreateDto createDto = EntityMock.createDto();

		String productJson = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productJson))
				.andExpect(status().isCreated());
	}

	@Test
	void testCreateProductInvalidDataException() throws Exception {
		ProductCreateDto createDto = EntityMock.createDto();
		createDto.setCategory("MOUSESES");

		String productJson = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid request content."))
				.andExpect(jsonPath("$.errors.category").value("must match \"COMPUTERS|SMARTPHONES|HEADPHONES|MOUSES|KEYBOARDS|SCREENS\""));
	}

	@Test
	void testCreateProductTitleUniqueViolationException() throws Exception {
		createClient();
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
		createClient();
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
		createClient();

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
		createClient();
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
		createClient();
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
		createClient();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/category/mouses")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindProductsByCategoryInvalidCategoryException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/category/mouse")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Category does not exist"));
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
		createClient();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/sku/MOUSE-2024-WL-0010")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindProductsBySkuEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/sku/MOUSE-2024-WL-0010")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Product not found by sku"));
	}

	@Test
	void testFindAllActivedSuccess() throws Exception {
		createClient();
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
		createClient();
		String after = LocalDate.now().minusDays(1).toString();
		String before = LocalDate.now().plusDays(1).toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/date?afterDate=" + after + "&beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindByDateAfterSuccess() throws Exception {
		createClient();
		String after = LocalDate.now().minusDays(1).toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/date?afterDate=" + after)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindByDateBeforeSuccess() throws Exception {
		createClient();
		String before = LocalDate.now().plusDays(1).toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/products/date?beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindByDateNoDateParametersProvided() throws Exception {
		createClient();
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
		createClient();
		ProductUpdateDto updateDto = EntityMock.productUpdateDto();

		String updateDtoJson = objectMapper.writeValueAsString(updateDto);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/edit/MOUSE-2024-WL-0010")
						.contentType(MediaType.APPLICATION_JSON)
						.content(updateDtoJson))
				.andExpect(status().isNoContent());
	}

	@Test
	void testEditProductDataBySkuInvalidDataException() throws Exception {
		createClient();
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

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/edit/MOUSE-2024-WL-0010")
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

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/edit/MOUSE-2024-WL-0010")
						.contentType(MediaType.APPLICATION_JSON)
						.content(updateDtoJson))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Product not found by sku"));
	}

	@Test
	void testToggleStatusBySkuSuccess() throws Exception {
		createClient();
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/status/MOUSE-2024-WL-0010")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value("updated status for: false"));
	}

	@Test
	void testToggleStatusBySkuInvalidProductStatusException() throws Exception {
		ProductCreateDto createDto = EntityMock.createDto();
		createDto.setQuantity(0);

		String productJson = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/products")
						.contentType(MediaType.APPLICATION_JSON)
						.content(productJson))
				.andExpect(status().isCreated());

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/status/MOUSE-2024-WL-0010")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Cannot alter status when quantity is 0"));
	}

	@Test
	void testToggleStatusBySkuEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/status/MOUSE-2024-WL-0010")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Product not found by sku"));
	}

	@Test
	void testAddProductBySkuSuccess() throws Exception {
		createClient();
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/sku/MOUSE-2024-WL-0010/add/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value("updated quantity for: 11"));
	}

	@Test
	void testAddProductBySkuInvalidQuantityException() throws Exception {
		createClient();
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/sku/MOUSE-2024-WL-0010/add/0")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("No quantity stated"));
	}

	@Test
	void testAddProductBySkuEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/sku/MOUSE-2024-WL-0010/add/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Product not found by sku"));
	}

	@Test
	void testSubProductBySkuSuccess() throws Exception {
		createClient();
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/sku/MOUSE-2024-WL-0010/sub/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value("updated quantity for: 9"));
	}

	@Test
	void testSubProductBySkuInvalidQuantityException() throws Exception {
		createClient();
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/sku/MOUSE-2024-WL-0010/sub/0")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("No quantity stated"));
	}

	@Test
	void testSubProductBySkuInvalidQuantityAvailableException() throws Exception {
		createClient();
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/sku/MOUSE-2024-WL-0010/sub/11")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("The quantity of available products is 10"));
	}

	@Test
	void testSubProductBySkuEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/products/sku/MOUSE-2024-WL-0010/sub/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Product not found by sku"));
	}

	@Test
	void testDeleteProductBySkuSuccess() throws Exception {
		createClient();
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/delete/MOUSE-2024-WL-0010")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}

	@Test
	void testDeleteProductBySkuEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/products/delete/MOUSE-2024-WL-0010")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Product not found by sku"));
	}
}
