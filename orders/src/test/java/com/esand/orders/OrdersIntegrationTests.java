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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

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

	@Test
	void testCreateOrderSuccess() throws Exception {
		OrderCreateDto orderCreateDto = EntityMock.createDto();
		Customer customer = EntityMock.customer();
		Product product = EntityMock.product();

		when(customerClient.getCustomerByCpf(any(String.class))).thenReturn(customer);
		when(productClient.getProductBySku(any(String.class))).thenReturn(product);

		String json = objectMapper.writeValueAsString(orderCreateDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/orders")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isCreated());
	}

	@Test
	void testCreateOrderClientNotFoundException() throws Exception {
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
				.andExpect(status().isNotFound()
				);
	}

	@Test
	void testCreateOrderClientServiceUnavailableException() throws Exception {
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
				.andExpect(status().isServiceUnavailable()
				);
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
				.andExpect(status().isNotFound()
				);
	}

	@Test
	void testCreateOrderProductServiceUnavailableException() throws Exception {
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
				.andExpect(status().isServiceUnavailable()
				);
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

		OrderResponseDto orderResponseDto = EntityMock.responseDto();

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].name").value(orderResponseDto.getName()))
				.andExpect(jsonPath("$.content[0].cpf").value(orderResponseDto.getCpf()))
				.andExpect(jsonPath("$.content[0].title").value(orderResponseDto.getTitle()))
				.andExpect(jsonPath("$.content[0].sku").value(orderResponseDto.getSku()))
				.andExpect(jsonPath("$.content[0].price").value(orderResponseDto.getPrice()))
				.andExpect(jsonPath("$.content[0].quantity").value(orderResponseDto.getQuantity()))
				.andExpect(jsonPath("$.content[0].total").value(orderResponseDto.getTotal()))
				.andExpect(jsonPath("$.content[0].processing").value(orderResponseDto.getProcessing()))
				.andExpect(jsonPath("$.content[0].date").isNotEmpty()
				);
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
		OrderResponseDto orderResponseDto = EntityMock.responseDto();

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/sku/" + orderResponseDto.getSku())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].name").value(orderResponseDto.getName()))
				.andExpect(jsonPath("$.content[0].cpf").value(orderResponseDto.getCpf()))
				.andExpect(jsonPath("$.content[0].title").value(orderResponseDto.getTitle()))
				.andExpect(jsonPath("$.content[0].sku").value(orderResponseDto.getSku()))
				.andExpect(jsonPath("$.content[0].price").value(orderResponseDto.getPrice()))
				.andExpect(jsonPath("$.content[0].quantity").value(orderResponseDto.getQuantity()))
				.andExpect(jsonPath("$.content[0].total").value(orderResponseDto.getTotal()))
				.andExpect(jsonPath("$.content[0].processing").value(orderResponseDto.getProcessing()))
				.andExpect(jsonPath("$.content[0].date").isNotEmpty()
				);
	}

	@Test
	void testFindBySkuOrderEntityNotFoundException() throws Exception {
		orderRepository.deleteAll();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/sku/" + EntityMock.order().getSku())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No orders found by sku")
				);
	}

	@Test
	void testFindByCpfOrdersSuccess() throws Exception {
		createOrder();
		OrderResponseDto orderResponseDto = EntityMock.responseDto();

		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/cpf/" + orderResponseDto.getCpf())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].name").value(orderResponseDto.getName()))
				.andExpect(jsonPath("$.content[0].cpf").value(orderResponseDto.getCpf()))
				.andExpect(jsonPath("$.content[0].title").value(orderResponseDto.getTitle()))
				.andExpect(jsonPath("$.content[0].sku").value(orderResponseDto.getSku()))
				.andExpect(jsonPath("$.content[0].price").value(orderResponseDto.getPrice()))
				.andExpect(jsonPath("$.content[0].quantity").value(orderResponseDto.getQuantity()))
				.andExpect(jsonPath("$.content[0].total").value(orderResponseDto.getTotal()))
				.andExpect(jsonPath("$.content[0].processing").value(orderResponseDto.getProcessing()))
				.andExpect(jsonPath("$.content[0].date").isNotEmpty()
				);
	}

	@Test
	void testFindByCpfOrderEntityNotFoundException() throws Exception {
		orderRepository.deleteAll();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/cpf/" + EntityMock.order().getCpf())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No orders found by cpf")
				);
	}

	@Test
	void testFindByDateBetweenSuccess() throws Exception {
		createOrder();
		OrderResponseDto orderResponseDto = EntityMock.responseDto();

		String after = LocalDate.now().minusDays(1).toString();
		String before = LocalDate.now().plusDays(1).toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/date?afterDate=" + after + "&beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].name").value(orderResponseDto.getName()))
				.andExpect(jsonPath("$.content[0].cpf").value(orderResponseDto.getCpf()))
				.andExpect(jsonPath("$.content[0].title").value(orderResponseDto.getTitle()))
				.andExpect(jsonPath("$.content[0].sku").value(orderResponseDto.getSku()))
				.andExpect(jsonPath("$.content[0].price").value(orderResponseDto.getPrice()))
				.andExpect(jsonPath("$.content[0].quantity").value(orderResponseDto.getQuantity()))
				.andExpect(jsonPath("$.content[0].total").value(orderResponseDto.getTotal()))
				.andExpect(jsonPath("$.content[0].processing").value(orderResponseDto.getProcessing()))
				.andExpect(jsonPath("$.content[0].date").isNotEmpty()
				);
	}

	@Test
	void testFindByDateAfterSuccess() throws Exception {
		createOrder();
		OrderResponseDto orderResponseDto = EntityMock.responseDto();

		String after = LocalDate.now().minusDays(1).toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/date?afterDate=" + after)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].name").value(orderResponseDto.getName()))
				.andExpect(jsonPath("$.content[0].cpf").value(orderResponseDto.getCpf()))
				.andExpect(jsonPath("$.content[0].title").value(orderResponseDto.getTitle()))
				.andExpect(jsonPath("$.content[0].sku").value(orderResponseDto.getSku()))
				.andExpect(jsonPath("$.content[0].price").value(orderResponseDto.getPrice()))
				.andExpect(jsonPath("$.content[0].quantity").value(orderResponseDto.getQuantity()))
				.andExpect(jsonPath("$.content[0].total").value(orderResponseDto.getTotal()))
				.andExpect(jsonPath("$.content[0].processing").value(orderResponseDto.getProcessing()))
				.andExpect(jsonPath("$.content[0].date").isNotEmpty()
				);
	}

	@Test
	void testFindByDateBeforeSuccess() throws Exception {
		createOrder();
		OrderResponseDto orderResponseDto = EntityMock.responseDto();

		String before = LocalDate.now().plusDays(1).toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/date?beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.content[0].name").value(orderResponseDto.getName()))
				.andExpect(jsonPath("$.content[0].cpf").value(orderResponseDto.getCpf()))
				.andExpect(jsonPath("$.content[0].title").value(orderResponseDto.getTitle()))
				.andExpect(jsonPath("$.content[0].sku").value(orderResponseDto.getSku()))
				.andExpect(jsonPath("$.content[0].price").value(orderResponseDto.getPrice()))
				.andExpect(jsonPath("$.content[0].quantity").value(orderResponseDto.getQuantity()))
				.andExpect(jsonPath("$.content[0].total").value(orderResponseDto.getTotal()))
				.andExpect(jsonPath("$.content[0].processing").value(orderResponseDto.getProcessing()))
				.andExpect(jsonPath("$.content[0].date").isNotEmpty()
				);
	}

	@Test
	void testFindByDateBetweenNoDateParametersProvided() throws Exception {
		createOrder();
		OrderResponseDto orderResponseDto = EntityMock.responseDto();

		String before = LocalDate.now().plusDays(1).toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/date?")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No date parameters provided")
				);
	}

	@Test
	void testFindByDateBetweenEntityNotFoundException() throws Exception {
		OrderResponseDto orderResponseDto = EntityMock.responseDto();

		String after = LocalDate.now().minusDays(1).toString();
		String before = LocalDate.now().plusDays(1).toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/orders/date?afterDate=" + after + "&beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No orders found by date(s)")
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
	void testSendOrderByIdClientNotFoundException() throws Exception {
		Order order = createOrder();

		HttpClientErrorException.NotFound feignException = mock(HttpClientErrorException.NotFound.class);
		when(customerClient.getCustomerByCpf(anyString())).thenThrow(feignException);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/orders/processing/" + order.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()
				);
	}

	@Test
	void testSendOrderByIdClientServiceUnavailableException() throws Exception {
		Order order = createOrder();

		HttpServerErrorException.ServiceUnavailable feignException = mock(HttpServerErrorException.ServiceUnavailable.class);
		when(customerClient.getCustomerByCpf(anyString())).thenThrow(feignException);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/orders/processing/" + order.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isServiceUnavailable()
				);
	}

	@Test
	void testSendOrderByIdProductNotFoundException() throws Exception {
		Order order = createOrder();

		HttpClientErrorException.NotFound feignException = mock(HttpClientErrorException.NotFound.class);
		when(productClient.getProductBySku(anyString())).thenThrow(feignException);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/orders/processing/" + order.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound()
				);
	}

	@Test
	void testSendOrderByIdProductServiceUnavailableException() throws Exception {
		Order order = createOrder();

		HttpServerErrorException.ServiceUnavailable feignException = mock(HttpServerErrorException.ServiceUnavailable.class);
		when(productClient.getProductBySku(anyString())).thenThrow(feignException);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/orders/processing/" + order.getId())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isServiceUnavailable()
				);
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
