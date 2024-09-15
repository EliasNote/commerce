package com.esand.customers;

import com.esand.customers.entity.EntityMock;
import com.esand.customers.repository.CustomerRepository;
import com.esand.customers.web.dto.CustomerCreateDto;
import com.esand.customers.web.dto.CustomerUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ClientIntegrationTests {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setUp() {
		customerRepository.deleteAll();
	}

	void createClient() {
		customerRepository.save(EntityMock.client());
	}

	@Test
	void testCreateClientSuccess() throws Exception {
		CustomerCreateDto createDto = EntityMock.createDto();

		String json = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/clients")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isCreated());
	}

	@Test
	void testCreateClientExceptionInvalidData() throws Exception {
		CustomerCreateDto createDto = EntityMock.createDto();
		createDto.setCpf("07021050071");

		String json = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/clients")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid request content."))
				.andExpect(jsonPath("$.errors.cpf").value("invalid Brazilian individual taxpayer registry number (CPF)"));
	}

	@Test
	void testCreateClientCpfUniqueViolationException() throws Exception {
		createClient();
		CustomerCreateDto createDto = EntityMock.createDto();

		String json = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/clients")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.message").value("CPF " + createDto.getCpf() +" cannot be registered, there is already a registered customer with an informed CPF"));
	}

	@Test
	void testFindAllClientsSuccess() throws Exception {
		createClient();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindAllClientsEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No customers found"));
	}

	@Test
	void testFindClientByNameSuccess() throws Exception {
		createClient();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/name/" + EntityMock.client().getName())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindClientByNameEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/name/Teste")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Customer not found by name"));
	}

	@Test
	void testFindClientByCpfSuccess() throws Exception {
		createClient();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/cpf/07021050070")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindClientByCpfNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/cpf/07021050070")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Customer not found by CPF"));
	}

	@Test
	void testFindClientsByDateBetweenSuccess() throws Exception {
		createClient();
		String after = LocalDate.now().minusDays(1).toString();
		String before = LocalDate.now().plusDays(1).toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/date?afterDate=" + after + "&beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindClientsByDateAfterSuccess() throws Exception {
		createClient();
		String after = LocalDate.now().minusDays(1).toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/date?afterDate=" + after)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindClientsByDateBeforeSuccess() throws Exception {
		createClient();
		String before = LocalDate.now().plusDays(1).toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/date?beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindClientsByDateNoDateParametersProvided() throws Exception {
		createClient();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/date?")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No date parameters provided"));
	}

	@Test
	void testFindClientsByDateEntityNotFoundException() throws Exception {
		String after = LocalDate.now().minusDays(1).toString();
		String before = LocalDate.now().plusDays(1).toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/date?afterDate=" + after + "&beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No clients found by date(s)"));
	}

	@Test
	void testEditClientDataByCpfSuccess() throws Exception {
		createClient();

		CustomerUpdateDto updateDto =  EntityMock.clientUpdateDto();
		updateDto.setName("Novo");

		String updateJson = objectMapper.writeValueAsString(updateDto);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/clients/edit/" + EntityMock.client().getCpf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson))
			.andExpect(status().isNoContent());
	}

	@Test
	void testEditClientInvalidDataException() throws Exception {
		createClient();

		CustomerUpdateDto updateDto =  EntityMock.clientUpdateDto();
		updateDto.setName("N");

		String updateJson = objectMapper.writeValueAsString(updateDto);


		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/clients/edit/07021050070")
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Invalid request content."))
			.andExpect(jsonPath("$.errors.name").value("size must be between 2 and 100"));
	}

	@Test
	void testEditClientNotFound() throws Exception {
		CustomerUpdateDto updateDto =  EntityMock.clientUpdateDto();
		updateDto.setName("Novo");

		String updateJson = objectMapper.writeValueAsString(updateDto);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/clients/edit/" + updateDto.getCpf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Customer not found by CPF"));
	}

	@Test
	void testDeleteClientSuccess() throws Exception {
		createClient();

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/clients/delete/07021050070")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}

	@Test
	void testDeleteClientNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/clients/delete/07021050070")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Customer not found by CPF"));
	}
}
