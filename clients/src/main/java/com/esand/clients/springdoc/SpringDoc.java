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
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Clients", description = "Contains all operations related to resources for registration, searching, correcting data, and updating status")
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
            @ApiResponse(responseCode = "200", description = "All customers found successfully",
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
            @ApiResponse(responseCode = "200", description = "Customers found successfully by name",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableDto.class))),
            @ApiResponse(responseCode = "404", description = "No customers found by name",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<PageableDto> findByName(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable, @PathVariable String name);

    @Operation(summary = "Search for customer by CPF",
            description = "Endpoint to search for a customer by CPF.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found successfully by CPF",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found by CPF",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<ClientResponseDto> findByCpf(@PathVariable String cpf);

    @Operation(summary = "Search for customers by date",
            description = "Endpoint to search for customers by date range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customers found successfully by date",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableDto.class))),
            @ApiResponse(responseCode = "404", description = "No customers found by date",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<PageableDto> findByDate(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                           @RequestParam(value = "afterDate", required = false) String afterDate,
                                           @RequestParam(value = "beforeDate", required = false) String beforeDate);

    @Operation(summary = "Update a customer's data",
            description = "Endpoint that updates the data of an existing customer by CPF.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer data updated successfully",
                    content = @Content),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Customer not found by CPF",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<Void> update(@PathVariable String cpf, @RequestBody @Valid ClientUpdateDto dto);

    @Operation(summary = "Delete a customer by CPF",
            description = "Endpoint that deletes a customer by CPF.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Customer not found by CPF",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<Void> deleteClientByCpf(@PathVariable String cpf);
}