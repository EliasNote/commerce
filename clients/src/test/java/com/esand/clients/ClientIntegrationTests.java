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

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

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
	void testeCriarClienteSucesso() throws Exception {
		ClientCreateDto createDto =  new ClientCreateDto("Teste", "07021050070", "55210568972", "teste@email.com", "Address111", LocalDate.of(2024, 8, 7), "M");

		String propostaJson = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/clients")
				.contentType(MediaType.APPLICATION_JSON)
				.content(propostaJson))
			.andExpect(status().isCreated());
	}

	@Test
	void testeCriarClienteExcecaoDadosInvalidos() throws Exception {
		ClientCreateDto createDto =  new ClientCreateDto("Teste", "07021050071", "55210568972", "teste@email.com", "Address111", LocalDate.of(2024, 8, 7), "M");

		String propostaJson = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/clients")
				.contentType(MediaType.APPLICATION_JSON)
				.content(propostaJson))
			.andExpect(status().isBadRequest());
	}

	@Test
	void testeCriarClienteCpfJaCadastrado() throws Exception {
		testeCriarClienteSucesso();
		ClientCreateDto createDto = new ClientCreateDto("Teste", "07021050070", "55210568972", "teste@email.com", "Address111", LocalDate.of(2024, 8, 7), "M");

		String propostaJson = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/clients")
						.contentType(MediaType.APPLICATION_JSON)
						.content(propostaJson))
				.andExpect(status().isConflict());
	}

	@Test
	void testeBuscarTodosOsClientesSucesso() throws Exception {
		testeCriarClienteSucesso();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testeBuscarTodosOsClientesNaoEncontrado() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void testeBuscarClientePorNomeSucesso() throws Exception {
		testeCriarClienteSucesso();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/name/Teste")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testeBuscarClientePorNomeNaoEncontrado() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/name/Teste")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void testeBuscarClientePorCpfSucesso() throws Exception {
		testeCriarClienteSucesso();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/cpf/07021050070")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testeBuscarClientePorCpfNaoEncontrado() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/cpf/07021050070")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void testeBuscarClientesPorDataSucesso() throws Exception {
		testeCriarClienteSucesso();
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/date?afterDate=2024-08-06")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void testeBuscarClientesPorDataNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/clients/date?afterDate=2024-08-06")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void testeEditarClienteSucesso() throws Exception {
		testeCriarClienteSucesso();

		ClientUpdateDto updateDto =  new ClientUpdateDto();
		updateDto.setName("Novo");

		String updateJson = objectMapper.writeValueAsString(updateDto);


		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/clients/edit/07021050070")
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson));
		result.andExpect(status().isNoContent());
	}

	@Test
	void testeEditarClienteDadosInvalidos() throws Exception {
		testeCriarClienteSucesso();

		ClientUpdateDto updateDto =  new ClientUpdateDto();
		updateDto.setName("N");

		String updateJson = objectMapper.writeValueAsString(updateDto);


		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/clients/edit/07021050070")
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson));
		result.andExpect(status().isBadRequest());
	}

	@Test
	void testeEditarClienteNaoEncontrado() throws Exception {

		ClientUpdateDto updateDto =  new ClientUpdateDto();
		updateDto.setName("Novo");

		String updateJson = objectMapper.writeValueAsString(updateDto);


		ResultActions result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/clients/edit/07021050070")
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson));
		result.andExpect(status().isNotFound());
	}
}
