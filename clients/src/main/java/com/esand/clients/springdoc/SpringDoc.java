package com.esand.clients.springdoc;

import com.esand.clients.web.dto.ClientCreateDto;
import com.esand.clients.web.dto.ClientResponseDto;
import com.esand.clients.web.dto.ClientUpdateDto;
import com.esand.clients.web.dto.PageableDto;
import com.esand.clients.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Clients", description = "Contains all operations related to resources for registration, searching, correcting data and updating status")
public interface SpringDoc {
    @Operation(summary = "Register a new customer",
            description = "Endpoint that registers a new customer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer registered successfully!",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "This customer has already been registered",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<ClientResponseDto> create(@RequestBody @Valid ClientCreateDto dto);

    @Operation(summary = "Search for all customers",
            description = "Endpoint to search for all customers.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all customers",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableDto.class))),
            @ApiResponse(responseCode = "404", description = "No customers found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<PageableDto> findAll(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable);

    @Operation(summary = "Search for customers by name",
            description = "Endpoint to search for customers by name.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved customers by name",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableDto.class))),
            @ApiResponse(responseCode = "404", description = "No customers found with the given name",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<PageableDto> findByName(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable, @PathVariable String name);

    @Operation(summary = "Search for customer by CPF",
            description = "Endpoint to search for a customer by CPF.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved customer by CPF",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "No customer found with the given CPF",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<ClientResponseDto> findByCpf(@PathVariable String cpf);

    @Operation(summary = "Update a customer's information",
            description = "Endpoint that updates the information of an existing customer by CPF.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer information updated successfully",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found with the given CPF",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<Void> update(@PathVariable String cpf, @RequestBody @Valid ClientUpdateDto dto);

    @Operation(summary = "Toggle the status of a customer",
            description = "Endpoint that toggles the status of an existing customer by CPF.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer status updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found with the given CPF",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<String> toggleStatus(@PathVariable String cpf);
}