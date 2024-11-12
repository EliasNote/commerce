package com.esand.customers;

import com.esand.customers.entity.Customer;
import com.esand.customers.entity.EntityMock;
import com.esand.customers.repository.CustomerRepository;
import com.esand.customers.web.dto.CustomerCreateDto;
import com.esand.customers.web.dto.CustomerResponseDto;
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
class CustomerIntegrationTests {

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@AfterEach
	public void setUp() {
		customerRepository.deleteAll();
	}

	Customer createCustomer() {
		return customerRepository.save(EntityMock.customer());
	}

	void verifyResult(ResultActions response, CustomerResponseDto responseDto, boolean isArray) throws Exception {
		String json = (isArray) ? ".content[0]" : "";

		response
				.andExpect(jsonPath("$" + json +  ".name").value(responseDto.getName()))
				.andExpect(jsonPath("$" + json +  ".cpf").value(responseDto.getCpf()))
				.andExpect(jsonPath("$" + json +  ".phone").value(responseDto.getPhone()))
				.andExpect(jsonPath("$" + json +  ".email").value(responseDto.getEmail()))
				.andExpect(jsonPath("$" + json +  ".address").value(responseDto.getAddress()))
				.andExpect(jsonPath("$" + json +  ".birthDate").value(responseDto.getBirthDate().toString()))
				.andExpect(jsonPath("$" + json +  ".gender").value(responseDto.getGender().toString())
		);
	}

	@Test
	void testCreateCustomerSuccess() throws Exception {
		CustomerCreateDto createDto = EntityMock.createDto();
		CustomerResponseDto responseDto = EntityMock.customerResponseDto();

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)))
			.andExpect(status().isCreated());

		verifyResult(response, responseDto, false);
	}

	@Test
	void testCreateCustomerExceptionInvalidData() throws Exception {
		CustomerCreateDto createDto = EntityMock.createDto();
		createDto.setCpf("07021050071");

		String json = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.message").value("Invalid request content."))
				.andExpect(jsonPath("$.errors.cpf").value("invalid Brazilian individual taxpayer registry number (CPF)"));
	}

	@Test
	void testCreateCustomerCpfUniqueViolationException() throws Exception {
		createCustomer();
		CustomerCreateDto createDto = EntityMock.createDto();

		String json = objectMapper.writeValueAsString(createDto);

		mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(json))
				.andExpect(status().isConflict())
				.andExpect(jsonPath("$.message").value("CPF " + createDto.getCpf() +" cannot be registered, there is already a registered customer with an informed CPF"));
	}

	@Test
	void testFindAllCustomersSuccess() throws Exception {
		createCustomer();
		CustomerResponseDto responseDto = EntityMock.customerResponseDto();

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verifyResult(response, responseDto, true);
	}

	@Test
	void testFindAllCustomersByDateBetweenSuccess() throws Exception {
		createCustomer();
		CustomerResponseDto responseDto = EntityMock.customerResponseDto();
		String after = LocalDate.now().minusDays(1).toString();
		String before = LocalDate.now().plusDays(1).toString();

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers?afterDate=" + after + "&beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verifyResult(response, responseDto, true);
	}

	@Test
	void testFindAllCustomersByDateAfterSuccess() throws Exception {
		createCustomer();
		CustomerResponseDto responseDto = EntityMock.customerResponseDto();
		String after = LocalDate.now().minusDays(1).toString();

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers?afterDate=" + after)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verifyResult(response, responseDto, true);
	}

	@Test
	void testFindAllCustomersByDateBeforeSuccess() throws Exception {
		createCustomer();
		CustomerResponseDto responseDto = EntityMock.customerResponseDto();
		String before = LocalDate.now().plusDays(1).toString();

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers?beforeDate=" + before)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verifyResult(response, responseDto, true);
	}

	@Test
	void testFindAllCustomersEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No customers found"));
	}

	@Test
	void testFindCustomerByNameSuccess() throws Exception {
		createCustomer();
		CustomerResponseDto responseDto = EntityMock.customerResponseDto();

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/name/" + EntityMock.customer().getName())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verifyResult(response, responseDto, true);
	}

    @Test
    void testFindCustomerByNameAndDateBetweenSuccess() throws Exception {
        createCustomer();
        CustomerResponseDto responseDto = EntityMock.customerResponseDto();
        String after = LocalDate.now().minusDays(1).toString();
        String before = LocalDate.now().plusDays(1).toString();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/name/" +
                                EntityMock.customer().getName() + "?afterDate=" + after + "&beforeDate=" + before)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verifyResult(response, responseDto, true);
    }

    @Test
    void testFindCustomerByNameAndDateAfterSuccess() throws Exception {
        createCustomer();
        CustomerResponseDto responseDto = EntityMock.customerResponseDto();
        String after = LocalDate.now().minusDays(1).toString();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/name/" +
                                EntityMock.customer().getName() + "?afterDate=" + after)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verifyResult(response, responseDto, true);
    }

    @Test
    void testFindCustomerByNameAndDateBeforeSuccess() throws Exception {
        createCustomer();
        CustomerResponseDto responseDto = EntityMock.customerResponseDto();
        String before = LocalDate.now().plusDays(1).toString();

        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/name/" +
                                EntityMock.customer().getName() + "?beforeDate=" + before)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verifyResult(response, responseDto, true);
    }

	@Test
	void testFindCustomerByNameEntityNotFoundException() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/name/Teste")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("No customers found"));
	}

	@Test
	void testFindCustomerByCpfSuccess() throws Exception {
		createCustomer();
		CustomerResponseDto responseDto = EntityMock.customerResponseDto();

		ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/cpf/07021050070")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		verifyResult(response, responseDto, false);
	}

	@Test
	void testFindCustomerByCpfNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/cpf/07021050070")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Customer not found by CPF"));
	}

	@Test
	void testUpdateCustomerDataByCpfSuccess() throws Exception {
		createCustomer();

		CustomerUpdateDto updateDto =  EntityMock.customerUpdateDto();
		updateDto.setName("Novo");

		String updateJson = objectMapper.writeValueAsString(updateDto);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/customers/edit/" + EntityMock.customer().getCpf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson))
			.andExpect(status().isNoContent());
	}

	@Test
	void testUpdateCustomerInvalidDataException() throws Exception {
		createCustomer();

		CustomerUpdateDto updateDto =  EntityMock.customerUpdateDto();
		updateDto.setName("N");

		String updateJson = objectMapper.writeValueAsString(updateDto);


		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/customers/edit/07021050070")
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").value("Invalid request content."))
			.andExpect(jsonPath("$.errors.name").value("size must be between 2 and 100"));
	}

	@Test
	void testUpdateCustomerNotFound() throws Exception {
		CustomerUpdateDto updateDto =  EntityMock.customerUpdateDto();
		updateDto.setName("Novo");

		String updateJson = objectMapper.writeValueAsString(updateDto);

		mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/customers/edit/" + updateDto.getCpf())
				.contentType(MediaType.APPLICATION_JSON)
				.content(updateJson))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").value("Customer not found by CPF"));
	}

	@Test
	void testDeleteCustomerSuccess() throws Exception {
		createCustomer();

		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/customers/delete/07021050070")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}

	@Test
	void testDeleteCustomerNotFound() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/customers/delete/07021050070")
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Customer not found by CPF"));
	}
}
