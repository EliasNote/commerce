package com.esand.clients;

import com.esand.clients.repository.ClientRepository;
import com.esand.clients.web.dto.ClientCreateDto;
import com.esand.clients.web.dto.ClientUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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
	private ClientRepository clientRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeEach
	public void setUp() {
		clientRepository.deleteAll();
	}

	@Test
	void testCreateClientSuccess() throws Exception {
		ClientCreateDto createDto =  new ClientCreateDto(
				"Teste",
				"07021050070",
				"55210568972",
				"teste@email.com",
				"Address111",
				LocalDate.of(2024, 8, 7),
				"M"
		);

		String propostaJson = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/clients")
				.contentType(MediaType.APPLICATION_JSON)
				.content(propostaJson))
			.andExpect(status().isCreated());
	}

	@Test
	void testCreateClientExceptionInvalidData() throws Exception {
		ClientCreateDto createDto =  new ClientCreateDto(
				"Teste",
				"07021050071",
				"55210568972",
				"teste@email.com",
				"Address111",
				LocalDate.of(2024, 8, 7),
				"M"
		);

		String propostaJson = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/clients")
				.contentType(MediaType.APPLICATION_JSON)
				.content(propostaJson))
			.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid request content."))
				.andExpect(jsonPath("$.errors.cpf").value("invalid Brazilian individual taxpayer registry number (CPF)"));
	}

	@Test
	void testeCreateClientCpfUniqueViolationException() throws Exception {
		testCreateClientSuccess();
		ClientCreateDto createDto = new ClientCreateDto(
				"Teste",
				"07021050070",
				"55210568972",
				"teste@email.com",
				"Address111",
				LocalDate.of(2024, 8, 7),
				"M"
		);

		String propostaJson = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/clients")
						.contentType(MediaType.APPLICATION_JSON)
						.content(propostaJson))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.message").value("CPF " + createDto.getCpf() +" cannot be registered, there is already a registered customer with an informed CPF"));
	}

	@Test
	void testFindAllClientsSuccess() throws Exception {
		testCreateClientSuccess();
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
		testCreateClientSuccess();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/name/Teste")
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
		testCreateClientSuccess();
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
		testCreateClientSuccess();
		String after = LocalDate.now().minusDays(1).toString();
		String before = LocalDate.now().plusDays(1).toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/date?afterDate=" + after + "&beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindClientsByDateAfterSuccess() throws Exception {
		testCreateClientSuccess();
		String after = LocalDate.now().minusDays(1).toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/date?afterDate=" + after)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindClientsByDateBeforeSuccess() throws Exception {
		testCreateClientSuccess();
		String before = LocalDate.now().plusDays(1).toString();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/date?beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindClientsByDateNoDateParametersProvided() throws Exception {
		testCreateClientSuccess();
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
		testCreateClientSuccess();

		ClientUpdateDto updateDto =  new ClientUpdateDto();
		updateDto.setName("Novo");

		String updateJson = objectMapper.writeValueAsString(updateDto);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/clients/edit/07021050070")
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson))
			.andExpect(status().isNoContent());
	}

	@Test
	void testEditClientInvalidDataException() throws Exception {
		testCreateClientSuccess();

		ClientUpdateDto updateDto =  new ClientUpdateDto();
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

		ClientUpdateDto updateDto =  new ClientUpdateDto();
		updateDto.setName("Novo");

		String updateJson = objectMapper.writeValueAsString(updateDto);


		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/clients/edit/07021050070")
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Customer not found by CPF"));
	}

	@Test
	void testDeleteClientSuccess() throws Exception {
		testCreateClientSuccess();

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/clients/delete/cpf/07021050070")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}

	@Test
	void testDeleteClientNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/clients/delete/cpf/07021050070")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Customer not found by CPF"));
	}
}
