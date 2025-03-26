package com.esand.delivery.springdoc;

import com.esand.delivery.entity.KeycloakAccess;
import com.esand.delivery.web.dto.DeliveryResponseDto;
import com.esand.delivery.web.dto.PageableDto;
import com.esand.delivery.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Deliveries", description = "Contains all operations related to delivery management, including searching, updating, deletion and Keycloak access")
public interface SpringDoc {

    @Operation(summary = "Search for all deliveries",
            description = "Endpoint to search for all deliveries.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All deliveries found successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableDto.class))),
            @ApiResponse(responseCode = "404", description = "No deliveries found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<PageableDto> findAll(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                        @RequestParam(value = "afterDate", required = false) String afterDate,
                                        @RequestParam(value = "beforeDate", required = false) String beforeDate);

    @Operation(summary = "Search for delivery by ID",
            description = "Endpoint to search for a delivery by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delivery found successfully by ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeliveryResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Delivery not found by ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<DeliveryResponseDto> findById(@PathVariable Long id);

    @Operation(summary = "Search for all shipped deliveries",
            description = "Endpoint to search for all shipped deliveries.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All shipped deliveries found successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableDto.class))),
            @ApiResponse(responseCode = "404", description = "No shipped deliveries found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<PageableDto> findAllShipped(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                               @RequestParam(value = "afterDate", required = false) String afterDate,
                                               @RequestParam(value = "beforeDate", required = false) String beforeDate);

    @Operation(summary = "Search for all processing deliveries",
            description = "Endpoint to search for all processing deliveries.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All processing deliveries found successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableDto.class))),
            @ApiResponse(responseCode = "404", description = "No processing deliveries found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<PageableDto> findAllProcessing(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                                  @RequestParam(value = "afterDate", required = false) String afterDate,
                                                  @RequestParam(value = "beforeDate", required = false) String beforeDate);

    @Operation(summary = "Search for all canceled deliveries",
            description = "Endpoint to search for all canceled deliveries.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All canceled deliveries found successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableDto.class))),
            @ApiResponse(responseCode = "404", description = "No canceled deliveries found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<PageableDto> findAllCanceled(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                                @RequestParam(value = "afterDate", required = false) String afterDate,
                                                @RequestParam(value = "beforeDate", required = false) String beforeDate);

    @Operation(summary = "Cancel a delivery",
            description = "Endpoint to cancel a delivery by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delivery canceled successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Delivery not found by ID or product not found by sku",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "Delivery already canceled",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "503", description = "Products API not available",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<String> cancelDelivery(@PathVariable Long id);

    @Operation(summary = "Mark delivery as shipped",
            description = "Endpoint to mark a delivery as shipped by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delivery marked as shipped successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Delivery not found by ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "409", description = "Delivery already shipped",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<String> productShipped(@PathVariable Long id);

    @Operation(summary = "Delete all canceled deliveries",
            description = "Endpoint to delete all deliveries that have been canceled.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "All canceled deliveries deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No canceled deliveries found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<Void> deleteAllCanceled();

    @Operation(summary = "Search deliveries by CPF",
            description = "Endpoint to search for deliveries by CPF.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deliveries found by CPF successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableDto.class))),
            @ApiResponse(responseCode = "404", description = "No deliveries found for the given CPF",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<PageableDto> findAllByCpf(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                             @RequestParam(value = "afterDate", required = false) String afterDate,
                                             @RequestParam(value = "beforeDate", required = false) String beforeDate,
                                             @PathVariable String cpf);

    @Operation(summary = "Search deliveries by SKU",
            description = "Endpoint to search for deliveries by SKU.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deliveries found by SKU successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PageableDto.class))),
            @ApiResponse(responseCode = "404", description = "No deliveries found for the given SKU",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<PageableDto> findAllBySku(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable,
                                             @RequestParam(value = "afterDate", required = false) String afterDate,
                                             @RequestParam(value = "beforeDate", required = false) String beforeDate,
                                             @PathVariable String sku);

    @Operation(summary = "List top shipped customers",
            description = "Endpoint to list top shipped customers based on total spent and quantity purchased.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top shipped customers retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "No shipped deliveries found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<String> findTopShippedCustomers(@RequestParam(value = "afterDate", required = false) String afterDate,
                                                   @RequestParam(value = "beforeDate", required = false) String beforeDate);

    @Operation(summary = "List top shipped products",
            description = "Endpoint to list top shipped products based on total revenue and quantity sold.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top shipped products retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "No shipped deliveries found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<String> findTopShippedProducts(@RequestParam(value = "afterDate", required = false) String afterDate,
                                                  @RequestParam(value = "beforeDate", required = false) String beforeDate);

    @Operation(summary = "Delete delivery by ID",
            description = "Endpoint to delete a delivery by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Delivery deleted successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Delivery not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class)))
    })
    ResponseEntity<Void> deleteById(@PathVariable Long id);
}