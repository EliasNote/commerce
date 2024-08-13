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
		ClientCreateDto createDto =  new ClientCreateDto("Teste", "07021050070", "55210568972", "teste@email.com", "Address111", LocalDate.of(2024, 8, 7), "M");

		String propostaJson = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/clients")
				.contentType(MediaType.APPLICATION_JSON)
				.content(propostaJson))
			.andExpect(status().isCreated());
	}

	@Test
	void testCreateClientExceptionInvalidData() throws Exception {
		ClientCreateDto createDto =  new ClientCreateDto("Teste", "07021050071", "55210568972", "teste@email.com", "Address111", LocalDate.of(2024, 8, 7), "M");

		String propostaJson = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/clients")
				.contentType(MediaType.APPLICATION_JSON)
				.content(propostaJson))
			.andExpect(status().isBadRequest());
	}

	@Test
	void testeCreateClientCpfUniqueViolationException() throws Exception {
		testCreateClientSuccess();
		ClientCreateDto createDto = new ClientCreateDto("Teste", "07021050070", "55210568972", "teste@email.com", "Address111", LocalDate.of(2024, 8, 7), "M");

		String propostaJson = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/clients")
						.contentType(MediaType.APPLICATION_JSON)
						.content(propostaJson))
				.andExpect(status().isConflict());
	}

	@Test
	void testFindAllClientsSuccess() throws Exception {
		testCreateClientSuccess();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindAllClientsNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void testFindClientByNameSuccess() throws Exception {
		testCreateClientSuccess();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/name/Teste")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindClientByNameNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/name/Teste")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
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
				.andExpect(status().isNotFound());
	}

	@Test
	void testFindClientsByDateSuccess() throws Exception {
		testCreateClientSuccess();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/date?afterDate=2024-08-06")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testFindClientsByDateNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/date?afterDate=2024-08-06")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void testEditClientSuccess() throws Exception {
		testCreateClientSuccess();

		ClientUpdateDto updateDto =  new ClientUpdateDto();
		updateDto.setName("Novo");

		String updateJson = objectMapper.writeValueAsString(updateDto);


		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/clients/edit/07021050070")
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson));
		result.andExpect(status().isNoContent());
	}

	@Test
	void testEditClientInvalidData() throws Exception {
		testCreateClientSuccess();

		ClientUpdateDto updateDto =  new ClientUpdateDto();
		updateDto.setName("N");

		String updateJson = objectMapper.writeValueAsString(updateDto);


		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/clients/edit/07021050070")
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson));
		result.andExpect(status().isBadRequest());
	}

	@Test
	void testEditClientNotFound() throws Exception {

		ClientUpdateDto updateDto =  new ClientUpdateDto();
		updateDto.setName("Novo");

		String updateJson = objectMapper.writeValueAsString(updateDto);


		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/clients/edit/07021050070")
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson));
		result.andExpect(status().isNotFound());
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
