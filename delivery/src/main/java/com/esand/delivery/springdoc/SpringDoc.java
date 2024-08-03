package com.esand.delivery.springdoc;

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

@Tag(name = "Deliveries", description = "Contains all operations related to delivery management, including searching, updating, and deletion")
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
    ResponseEntity<PageableDto> findAll(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable);

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
    ResponseEntity<PageableDto> findAllShipped(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable);

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
    ResponseEntity<PageableDto> findAllProcessing(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable);

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
    ResponseEntity<PageableDto> findAllCanceled(@Parameter(hidden = true) @PageableDefault(size = 10) Pageable pageable);

    @Operation(summary = "Cancel a delivery",
            description = "Endpoint to cancel a delivery by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delivery canceled successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "404", description = "Delivery not found by ID",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))),
            @ApiResponse(responseCode = "404", description = "Product not found by sku, but status has been updated",
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
                    content = @Content)
    })
    ResponseEntity<Void> deleteAllCanceled();
}
