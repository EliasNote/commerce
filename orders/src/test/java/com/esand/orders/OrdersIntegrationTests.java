package com.esand.orders;

import com.esand.orders.client.customers.Customer;
import com.esand.orders.client.customers.CustomerClient;
import com.esand.orders.client.products.Product;
import com.esand.orders.client.products.ProductClient;
import com.esand.orders.entity.EntityMock;
import com.esand.orders.entity.Order;
import com.esand.orders.repository.order.OrderRepository;
import com.esand.orders.web.dto.OrderCreateDto;
import com.esand.orders.web.dto.OrderResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrdersIntegrationTests {

	@Value("${topic_name}")
	private String topicName;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ProductClient productClient;

	@MockBean
	private CustomerClient customerClient;

	@MockBean
	private Keycloak keycloak;

	@MockBean
	private KafkaTemplate<String, Serializable> kafkaTemplate;

	@MockBean
	private KafkaAdmin kafkaAdmin;

	@BeforeEach
	public void setUp() {
		orderRepository.deleteAll();

		TokenManager tokenManager = mock(TokenManager.class);
		AccessTokenResponse tokenResponse = new AccessTokenResponse();
		tokenResponse.setToken("mocked-access-token");
		tokenResponse.setTokenType("Bearer");

		when(tokenManager.getAccessToken()).thenReturn(tokenResponse);
		when(keycloak.tokenManager()).thenReturn(tokenManager);
	}

	Order createOrder() {
		return orderRepository.save(EntityMock.order());
	}

	void verifyResult(ResultActions response, OrderResponseDto responseDto, boolean isArray) throws Exception {
		String json = (isArray) ? ".content[0]" : "";

		response
				.andExpect(jsonPath("$" + json +  ".name").value(responseDto.getName()))
				.andExpect(jsonPath("$" + json +  ".cpf").value(responseDto.getCpf()))
				.andExpect(jsonPath("$" + json +  ".title").value(responseDto.getTitle()))
				.andExpect(jsonPath("$" + json +  ".sku").value(responseDto.getSku()))
				.andExpect(jsonPath("$" + json +  ".price").value(responseDto.getPrice()))
				.andExpect(jsonPath("$" + json +  ".quantity").value(responseDto.getQuantity()))
				.andExpect(jsonPath("$" + json +  ".total").value(responseDto.getTotal()))
				.andExpect(jsonPath("$" + json +  ".processing").value(responseDto.getProcessing()))
				.andExpect(jsonPath("$" + json +  ".date").isNotEmpty()
				);
	}

	@Test
	void testCreateOrderSuccess() throws Exception {
		OrderCreateDto orderCreateDto = EntityMock.createDto();
		OrderResponseDto responseDto = EntityMock.responseDto();
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		String json = objectMapper.writeValueAsString(orderCreateDto);

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isCreated());

		verifyResult(response, responseDto, false);
	}

	@Test
	void testCreateOrderCustomerNotFoundException() throws Exception {
		OrderCreateDto orderCreateDto = EntityMock.createDto();
		Customer customer = new Customer();
		Product product = EntityMock.product();
		product.setStatus(false);

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		HttpClientErrorException.NotFound notFoundException = mock(HttpClientErrorException.NotFound.class);
		when(customerClient.getCustomerByCpf(anyString())).thenThrow(notFoundException);

		String json = objectMapper.writeValueAsString(orderCreateDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Customer not found by CPF"));
	}

	@Test
	void testCreateOrderCustomerConnectionException() throws Exception {
		OrderCreateDto orderCreateDto = EntityMock.createDto();
		Customer customer = new Customer();
		Product product = EntityMock.product();
		product.setStatus(false);

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		HttpServerErrorException.ServiceUnavailable connectionException = mock(HttpServerErrorException.ServiceUnavailable.class);
		when(customerClient.getCustomerByCpf(anyString())).thenThrow(connectionException);

		String json = objectMapper.writeValueAsString(orderCreateDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isServiceUnavailable())
				.andExpect(jsonPath("$.message").value("Customers API not available"));
	}

	@Test
	void testCreateOrderCustomerUnknownErrorException() throws Exception {
		OrderCreateDto orderCreateDto = EntityMock.createDto();
		Customer customer = new Customer();
		Product product = EntityMock.product();
		product.setStatus(false);

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		RestClientException connectionException = mock(RestClientException.class);
		when(customerClient.getCustomerByCpf(anyString())).thenThrow(connectionException);

		String json = objectMapper.writeValueAsString(orderCreateDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.message").value("Error fetching customer by CPF: null"));
	}

	@Test
	void testCreateOrderProductNotFoundException() throws Exception {
		OrderCreateDto orderCreateDto = EntityMock.createDto();
		Customer customer = new Customer();
		Product product = EntityMock.product();
		product.setStatus(false);

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		HttpClientErrorException.NotFound notFoundException = mock(HttpClientErrorException.NotFound.class);
		when(productClient.getProductBySku(anyString())).thenThrow(notFoundException);

		String json = objectMapper.writeValueAsString(orderCreateDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Product not found by SKU"));
	}

	@Test
	void testCreateOrderProductConnectionException() throws Exception {
		OrderCreateDto orderCreateDto = EntityMock.createDto();
		Customer customer = new Customer();
		Product product = EntityMock.product();
		product.setStatus(false);

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		HttpServerErrorException.ServiceUnavailable connectionException = mock(HttpServerErrorException.ServiceUnavailable.class);
		when(productClient.getProductBySku(anyString())).thenThrow(connectionException);

		String json = objectMapper.writeValueAsString(orderCreateDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isServiceUnavailable())
				.andExpect(jsonPath("$.message").value("Products API not available"));
	}

	@Test
	void testCreateOrderProductUnknownErrorException() throws Exception {
		OrderCreateDto orderCreateDto = EntityMock.createDto();
		Customer customer = new Customer();
		Product product = EntityMock.product();
		product.setStatus(false);

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		RestClientException connectionException = mock(RestClientException.class);
		when(productClient.getProductBySku(anyString())).thenThrow(connectionException);

		String json = objectMapper.writeValueAsString(orderCreateDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.message").value("Error fetching product by SKU: null"));
	}

	@Test
	void testCreateOrderExceptionInvalidData() throws Exception {
		OrderCreateDto orderCreateDto = EntityMock.createDto();
		orderCreateDto.setQuantity(0);
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		String json = objectMapper.writeValueAsString(orderCreateDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("No quantity stated")
		);
	}

	@Test
	void testCreateOrderInvalidQuantityAvailableException() throws Exception {
		OrderCreateDto orderCreateDto = EntityMock.createDto();
		orderCreateDto.setQuantity(11);
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		String json = objectMapper.writeValueAsString(orderCreateDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("The quantity of available products is " + product.getQuantity())
				);
	}

	@Test
	void testCreateOrderUnavailableProductException() throws Exception {
		OrderCreateDto orderCreateDto = EntityMock.createDto();
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();
		product.setStatus(false);

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		String json = objectMapper.writeValueAsString(orderCreateDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("The product is not available")
				);
	}

	@Test
	void testFindAllOrdersSuccess() throws Exception {
		createOrder();
		OrderResponseDto responseDto = EntityMock.responseDto();
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verifyResult(response, responseDto, true);
	}

	@Test
	void testFindAllByDateBetweenSuccess() throws Exception {
		createOrder();
		OrderResponseDto responseDto = EntityMock.responseDto();
		String after = LocalDate.now().minusDays(1).toString();
		String before = LocalDate.now().plusDays(1).toString();
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders?afterDate=" + after + "&beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verifyResult(response, responseDto, true);
	}

	@Test
	void testFindAllByDateAfterSuccess() throws Exception {
		createOrder();
		OrderResponseDto responseDto = EntityMock.responseDto();
		String after = LocalDate.now().minusDays(1).toString();
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders?afterDate=" + after)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verifyResult(response, responseDto, true);
	}

	@Test
	void testFindAllByDateBeforeSuccess() throws Exception {
		createOrder();
		OrderResponseDto responseDto = EntityMock.responseDto();
		String before = LocalDate.now().plusDays(1).toString();
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders?beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verifyResult(response, responseDto, true);
	}

	@Test
	void testFindAllOrdersEntityNotFoundException() throws Exception {
		orderRepository.deleteAll();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No orders found")
				);
	}

	@Test
	void testFindBySkuOrdersSuccess() throws Exception {
		createOrder();
		OrderResponseDto responseDto = EntityMock.responseDto();
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/sku/" + responseDto.getSku())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verifyResult(response, responseDto, true);
	}

	@Test
	void testFindBySkuAndDateBetweenSuccess() throws Exception {
		createOrder();
		OrderResponseDto responseDto = EntityMock.responseDto();
		String after = LocalDate.now().minusDays(1).toString();
		String before = LocalDate.now().plusDays(1).toString();
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/sku/" + EntityMock.PRODUCT_SKU + "?afterDate=" + after + "&beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verifyResult(response, responseDto, true);
	}

	@Test
	void testFindBySkuAndDateAfterSuccess() throws Exception {
		createOrder();
		OrderResponseDto responseDto = EntityMock.responseDto();
		String after = LocalDate.now().minusDays(1).toString();
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/sku/" + EntityMock.PRODUCT_SKU + "?afterDate=" + after)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verifyResult(response, responseDto, true);
	}

	@Test
	void testFindBySkuAndDateBeforeSuccess() throws Exception {
		createOrder();
		OrderResponseDto responseDto = EntityMock.responseDto();
		String before = LocalDate.now().plusDays(1).toString();
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/sku/" + EntityMock.PRODUCT_SKU + "?beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verifyResult(response, responseDto, true);
	}

	@Test
	void testFindBySkuOrderEntityNotFoundException() throws Exception {
		orderRepository.deleteAll();


		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/sku/" + EntityMock.order().getSku())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No orders found")
				);
	}

	@Test
	void testFindByCpfOrdersSuccess() throws Exception {
		createOrder();
		OrderResponseDto responseDto = EntityMock.responseDto();
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/cpf/" + responseDto.getCpf())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verifyResult(response, responseDto, true);
	}

	@Test
	void testFindByCpfAndDateBetweenSuccess() throws Exception {
		createOrder();
		OrderResponseDto responseDto = EntityMock.responseDto();
		String after = LocalDate.now().minusDays(1).toString();
		String before = LocalDate.now().plusDays(1).toString();
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/cpf/" + EntityMock.CUSTOMER_CPF + "?afterDate=" + after + "&beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verifyResult(response, responseDto, true);
	}

	@Test
	void testFindByCpfAndDateAfterSuccess() throws Exception {
		createOrder();
		OrderResponseDto responseDto = EntityMock.responseDto();
		String after = LocalDate.now().minusDays(1).toString();
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/cpf/" + EntityMock.CUSTOMER_CPF + "?afterDate=" + after)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verifyResult(response, responseDto, true);
	}

	@Test
	void testFindByCpfAndDateBeforeSuccess() throws Exception {
		createOrder();
		OrderResponseDto responseDto = EntityMock.responseDto();
		String before = LocalDate.now().plusDays(1).toString();
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/cpf/" + EntityMock.CUSTOMER_CPF + "?beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verifyResult(response, responseDto, true);
	}

	@Test
	void testFindByCpfOrderEntityNotFoundException() throws Exception {
		orderRepository.deleteAll();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/cpf/" + EntityMock.order().getCpf())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No orders found")
				);
	}

	@Test
	void testDeleteByIdSuccess() throws Exception {
		Order order = createOrder();

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/orders/delete/" + order.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent()
				);
	}

	@Test
	void testDeleteByIdEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/orders/delete/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Order not found")
				);
	}

	@Test
	void testDeleteAllProcessingSuccess() throws Exception {
		Order order = createOrder();
		order.setProcessing(true);
		orderRepository.save(order);

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/orders/delete/processing")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent()
				);
	}

	@Test
	void testDeleteAllProcessingEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/orders/delete/processing")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No orders processing found")
				);
	}

	@Test
	void testSendOrderByIdSuccess() throws Exception {
		Order order = createOrder();
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);
		doNothing().when(productClient).decreaseProductQuantityBySku(any(String.class), any(Integer.class));

		CompletableFuture<SendResult<String, Serializable>> future = CompletableFuture.completedFuture(mock(SendResult.class));
		when(kafkaTemplate.send(any(String.class), any(OrderResponseDto.class))).thenReturn(future);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/orders/processing/" + order.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").value("Order nº" + order.getId() + " is processing successfully")
				);
	}

	@Test
	void testSendOrderByIdEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/orders/processing/1")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Order nº1 does not exist")
				);
	}

	@Test
	void testSendOrderByIdOrderAlreadySentException() throws Exception {
		Order order = createOrder();
		order.setProcessing(true);
		orderRepository.save(order);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/orders/processing/" + order.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.message").value("Already processing order")
				);
	}

	@Test
	void testSendOrderByIdCustomerNotFoundException() throws Exception {
		Order order = createOrder();

		HttpClientErrorException.NotFound feignException = mock(HttpClientErrorException.NotFound.class);
		when(customerClient.getCustomerByCpf(anyString())).thenThrow(feignException);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/orders/processing/" + order.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Customer not found by CPF"));
	}

	@Test
	void testSendOrderByIdCustomerConnectionException() throws Exception {
		Order order = createOrder();

		HttpServerErrorException.ServiceUnavailable feignException = mock(HttpServerErrorException.ServiceUnavailable.class);
		when(customerClient.getCustomerByCpf(anyString())).thenThrow(feignException);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/orders/processing/" + order.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isServiceUnavailable())
				.andExpect(jsonPath("$.message").value("Customers API not available"));
	}

	@Test
	void testSendOrderByIdCustomerUnknownErrorException() throws Exception {
		Order order = createOrder();

		RestClientException connectionException = mock(RestClientException.class);
		when(customerClient.getCustomerByCpf(anyString())).thenThrow(connectionException);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/orders/processing/" + order.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.message").value("Error fetching customer by CPF: null"));
	}

	@Test
	void testSendOrderByIdProductNotFoundException() throws Exception {
		Order order = createOrder();

		HttpClientErrorException.NotFound feignException = mock(HttpClientErrorException.NotFound.class);
		when(productClient.getProductBySku(anyString())).thenThrow(feignException);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/orders/processing/" + order.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Product not found by SKU"));
	}

	@Test
	void testSendOrderByIdProductConnectionException() throws Exception {
		Order order = createOrder();

		HttpServerErrorException.ServiceUnavailable feignException = mock(HttpServerErrorException.ServiceUnavailable.class);
		when(productClient.getProductBySku(anyString())).thenThrow(feignException);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/orders/processing/" + order.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isServiceUnavailable())
				.andExpect(jsonPath("$.message").value("Products API not available"));
	}

	@Test
	void testSendOrderByIdProductUnknownErrorException() throws Exception {
		Order order = createOrder();

		RestClientException connectionException = mock(RestClientException.class);
		when(productClient.getProductBySku(anyString())).thenThrow(connectionException);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/orders/processing/" + order.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.message").value("Error fetching product by SKU: null"));
	}

	@Test
	void testSendOrderByIdInvalidQuantityException() throws Exception {
		Order order = createOrder();
		order.setQuantity(0);
		orderRepository.save(order);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/orders/processing/" + order.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("No quantity stated")
				);
	}

	@Test
	void testSendOrderByIdInvalidQuantityAvailableException() throws Exception {
		Order order = createOrder();
		order.setQuantity(11);
		orderRepository.save(order);
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/orders/processing/" + order.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("The quantity of available products is " + product.getQuantity())
				);
	}

	@Test
	void testSendOrderByIdUnavailableProductException() throws Exception {
		Order order = createOrder();
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();
		product.setStatus(false);

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/orders/processing/" + order.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("The product is not available")
				);
	}
}
