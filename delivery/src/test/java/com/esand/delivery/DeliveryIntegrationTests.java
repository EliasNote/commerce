package com.esand.delivery;

import com.esand.delivery.client.products.ProductClient;
import com.esand.delivery.config.Listener;
import com.esand.delivery.entity.Delivery;
import com.esand.delivery.entity.EntityMock;
import com.esand.delivery.repository.DeliveryRepository;
import com.esand.delivery.web.dto.DeliveryResponseDto;
import com.esand.delivery.web.dto.DeliverySaveDto;
import com.esand.delivery.web.mapper.DeliveryMapper;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.swing.text.html.parser.Entity;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DeliveryIntegrationTests {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private DeliveryMapper deliveryMapper;

    @MockBean
    private ProductClient productClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Listener listener;

    @BeforeEach
    public void setUp() {
        deliveryRepository.deleteAll();
    }

    Delivery createDelivery() {
        return deliveryRepository.save(EntityMock.delivery());
    }

    @Test
    void testSaveDeliverySuccess() {
        DeliverySaveDto deliverySaveDto = EntityMock.saveDto();

        listener.consumer(deliverySaveDto);

        Delivery savedDelivery = deliveryRepository.findById(deliverySaveDto.getId()).orElse(null);
        assertNotNull(savedDelivery);
        assertEquals(deliverySaveDto.getId(), savedDelivery.getId());
        assertEquals(deliverySaveDto.getName(), savedDelivery.getName());
        assertEquals(deliverySaveDto.getCpf(), savedDelivery.getCpf());
        assertEquals(deliverySaveDto.getTitle(), savedDelivery.getTitle());
        assertEquals(deliverySaveDto.getSku(), savedDelivery.getSku());
        assertEquals(deliverySaveDto.getPrice(), savedDelivery.getPrice());
        assertEquals(deliverySaveDto.getQuantity(), savedDelivery.getQuantity());
        assertEquals(deliverySaveDto.getTotal(), savedDelivery.getTotal());
    }

    @Test
    void testFindAllDeliverySuccess() throws Exception{
        createDelivery();

        DeliveryResponseDto responseDto = EntityMock.responseDto();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].purchaser").value(responseDto.getName()))
                .andExpect(jsonPath("$.content[0].CPF").value(responseDto.getCpf()))
                .andExpect(jsonPath("$.content[0]['product name']").value(responseDto.getTitle()))
                .andExpect(jsonPath("$.content[0].SKU").value(responseDto.getSku()))
                .andExpect(jsonPath("$.content[0]['unit price']").value(responseDto.getPrice()))
                .andExpect(jsonPath("$.content[0].quantity").value(responseDto.getQuantity()))
                .andExpect(jsonPath("$.content[0]['total price']").value(responseDto.getTotal()))
                .andExpect(jsonPath("$.content[0].status").value(responseDto.getStatus()))
                .andExpect(jsonPath("$.content[0]['purchase date']").isNotEmpty()
                );
    }

    @Test
    void testFindAllDeliveryEntityNotFoundException() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/deliveries")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No orders found")
                );
    }

    @Test
    void testFindByIdSuccess() throws Exception {
        createDelivery();

        DeliveryResponseDto responseDto = EntityMock.responseDto();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/deliveries/id/" + responseDto.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.purchaser").value(responseDto.getName()))
                .andExpect(jsonPath("$.CPF").value(responseDto.getCpf()))
                .andExpect(jsonPath("$['product name']").value(responseDto.getTitle()))
                .andExpect(jsonPath("$.SKU").value(responseDto.getSku()))
                .andExpect(jsonPath("$['unit price']").value(responseDto.getPrice()))
                .andExpect(jsonPath("$.quantity").value(responseDto.getQuantity()))
                .andExpect(jsonPath("$['total price']").value(responseDto.getTotal()))
                .andExpect(jsonPath("$.status").value(responseDto.getStatus()))
                .andExpect(jsonPath("$['purchase date']").isNotEmpty()
                );
    }

    @Test
    void testFindByIdEntityNotFoundException() throws Exception {
        DeliveryResponseDto responseDto = EntityMock.responseDto();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/deliveries/id/" + responseDto.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order nº" + responseDto.getId() + " does not exist")
                );
    }

    @Test
    void testFindAllShippedSuccess() throws Exception{
        Delivery delivery = EntityMock.delivery();
        delivery.setStatus(Delivery.Status.SHIPPED);
        deliveryRepository.save(delivery);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/deliveries/shipped")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].purchaser").value(delivery.getName()))
                .andExpect(jsonPath("$.content[0].CPF").value(delivery.getCpf()))
                .andExpect(jsonPath("$.content[0]['product name']").value(delivery.getTitle()))
                .andExpect(jsonPath("$.content[0].SKU").value(delivery.getSku()))
                .andExpect(jsonPath("$.content[0]['unit price']").value(delivery.getPrice()))
                .andExpect(jsonPath("$.content[0].quantity").value(delivery.getQuantity()))
                .andExpect(jsonPath("$.content[0]['total price']").value(delivery.getTotal()))
                .andExpect(jsonPath("$.content[0].status").value(delivery.getStatus().toString()))
                .andExpect(jsonPath("$.content[0]['purchase date']").isNotEmpty()
                );
    }

    @Test
    void testFindAllShippedEntityNotFoundException() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/deliveries/shipped")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No orders found")
                );
    }

    @Test
    void testFindAllProcessingSuccess() throws Exception{
        createDelivery();

        DeliveryResponseDto responseDto = EntityMock.responseDto();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/deliveries/processing")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].purchaser").value(responseDto.getName()))
                .andExpect(jsonPath("$.content[0].CPF").value(responseDto.getCpf()))
                .andExpect(jsonPath("$.content[0]['product name']").value(responseDto.getTitle()))
                .andExpect(jsonPath("$.content[0].SKU").value(responseDto.getSku()))
                .andExpect(jsonPath("$.content[0]['unit price']").value(responseDto.getPrice()))
                .andExpect(jsonPath("$.content[0].quantity").value(responseDto.getQuantity()))
                .andExpect(jsonPath("$.content[0]['total price']").value(responseDto.getTotal()))
                .andExpect(jsonPath("$.content[0].status").value(responseDto.getStatus()))
                .andExpect(jsonPath("$.content[0]['purchase date']").isNotEmpty()
                );
    }

    @Test
    void testFindAllProcessingEntityNotFoundException() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/deliveries/processing")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No orders found")
                );
    }

    @Test
    void testFindAllCanceledSuccess() throws Exception{
        Delivery delivery = EntityMock.delivery();
        delivery.setStatus(Delivery.Status.CANCELED);
        deliveryRepository.save(delivery);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/deliveries/canceled")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].purchaser").value(delivery.getName()))
                .andExpect(jsonPath("$.content[0].CPF").value(delivery.getCpf()))
                .andExpect(jsonPath("$.content[0]['product name']").value(delivery.getTitle()))
                .andExpect(jsonPath("$.content[0].SKU").value(delivery.getSku()))
                .andExpect(jsonPath("$.content[0]['unit price']").value(delivery.getPrice()))
                .andExpect(jsonPath("$.content[0].quantity").value(delivery.getQuantity()))
                .andExpect(jsonPath("$.content[0]['total price']").value(delivery.getTotal()))
                .andExpect(jsonPath("$.content[0].status").value(delivery.getStatus().toString()))
                .andExpect(jsonPath("$.content[0]['purchase date']").isNotEmpty()
                );
    }

    @Test
    void testFindAllCanceledEntityNotFoundException() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/deliveries/canceled")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No orders found")
                );
    }

    @Test
    void testFindByDateBetweenSuccess() throws Exception {
        createDelivery();
        DeliveryResponseDto responseDto = EntityMock.responseDto();

        String after = LocalDate.now().minusDays(1).toString();
        String before = LocalDate.now().plusDays(1).toString();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/deliveries/date?afterDate=" + after + "&beforeDate=" + before)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].purchaser").value(responseDto.getName()))
                .andExpect(jsonPath("$.content[0].CPF").value(responseDto.getCpf()))
                .andExpect(jsonPath("$.content[0]['product name']").value(responseDto.getTitle()))
                .andExpect(jsonPath("$.content[0].SKU").value(responseDto.getSku()))
                .andExpect(jsonPath("$.content[0]['unit price']").value(responseDto.getPrice()))
                .andExpect(jsonPath("$.content[0].quantity").value(responseDto.getQuantity()))
                .andExpect(jsonPath("$.content[0]['total price']").value(responseDto.getTotal()))
                .andExpect(jsonPath("$.content[0].status").value(responseDto.getStatus()))
                .andExpect(jsonPath("$.content[0]['purchase date']").isNotEmpty()
                );
    }

    @Test
    void testFindByDateAfterSuccess() throws Exception {
        createDelivery();
        DeliveryResponseDto responseDto = EntityMock.responseDto();

        String after = LocalDate.now().minusDays(1).toString();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/deliveries/date?afterDate=" + after)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].purchaser").value(responseDto.getName()))
                .andExpect(jsonPath("$.content[0].CPF").value(responseDto.getCpf()))
                .andExpect(jsonPath("$.content[0]['product name']").value(responseDto.getTitle()))
                .andExpect(jsonPath("$.content[0].SKU").value(responseDto.getSku()))
                .andExpect(jsonPath("$.content[0]['unit price']").value(responseDto.getPrice()))
                .andExpect(jsonPath("$.content[0].quantity").value(responseDto.getQuantity()))
                .andExpect(jsonPath("$.content[0]['total price']").value(responseDto.getTotal()))
                .andExpect(jsonPath("$.content[0].status").value(responseDto.getStatus()))
                .andExpect(jsonPath("$.content[0]['purchase date']").isNotEmpty()
                );
    }

    @Test
    void testFindByDateBeforeSuccess() throws Exception {
        createDelivery();
        DeliveryResponseDto responseDto = EntityMock.responseDto();

        String before = LocalDate.now().plusDays(1).toString();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/deliveries/date?beforeDate=" + before)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].purchaser").value(responseDto.getName()))
                .andExpect(jsonPath("$.content[0].CPF").value(responseDto.getCpf()))
                .andExpect(jsonPath("$.content[0]['product name']").value(responseDto.getTitle()))
                .andExpect(jsonPath("$.content[0].SKU").value(responseDto.getSku()))
                .andExpect(jsonPath("$.content[0]['unit price']").value(responseDto.getPrice()))
                .andExpect(jsonPath("$.content[0].quantity").value(responseDto.getQuantity()))
                .andExpect(jsonPath("$.content[0]['total price']").value(responseDto.getTotal()))
                .andExpect(jsonPath("$.content[0].status").value(responseDto.getStatus()))
                .andExpect(jsonPath("$.content[0]['purchase date']").isNotEmpty()
                );
    }

    @Test
    void testFindByDateBetweenNoDateParametersProvided() throws Exception {
        createDelivery();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/deliveries/date?")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No date parameters provided")
                );
    }

    @Test
    void testFindByDateBetweenEntityNotFoundException() throws Exception {
        String after = LocalDate.now().minusDays(1).toString();
        String before = LocalDate.now().plusDays(1).toString();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/deliveries/date?afterDate=" + after + "&beforeDate=" + before)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No deliveries found by date(s)")
                );
    }

    @Test
    void testCancelDeliveryByIdSuccess() throws Exception{
        Delivery delivery = createDelivery();

        doNothing().when(productClient).checkStatus();
        doNothing().when(productClient).addProductQuantityBySku(any(String.class), any(Integer.class));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/deliveries/cancel/" + delivery.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Order nº" + delivery.getId() + " status changed to canceled successfully")
                );
    }

    @Test
    void testCancelDeliveryByIdEntityNotFoundException() throws Exception{
        Delivery delivery = EntityMock.delivery();

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/deliveries/cancel/" + delivery.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Order nº" + delivery.getId() + " does not exist")
                );
    }

    @Test
    void testCancelDeliveryByIdOrderCanceledException() throws Exception{
        Delivery delivery = EntityMock.delivery();
        delivery.setStatus(Delivery.Status.CANCELED);
        deliveryRepository.save(delivery);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/deliveries/cancel/" + delivery.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Order nº" + delivery.getId() + " has already been canceled")
                );
    }

    @Test
    void testCancelDeliveryByIdProductFeignConnectionException() throws Exception{
        Delivery delivery = createDelivery();

        FeignException.ServiceUnavailable feignException = mock(FeignException.ServiceUnavailable.class);
        doThrow(feignException).when(productClient).checkStatus();

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/deliveries/cancel/" + delivery.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable()
                );
    }

    @Test
    void testCancelDeliveryByIdProductFeignEntityNotFoundException() throws Exception{
        Delivery delivery = createDelivery();

        FeignException.NotFound feignException = mock(FeignException.NotFound.class);
        doNothing().when(productClient).checkStatus();
        doThrow(feignException).when(productClient).addProductQuantityBySku(any(String.class), any(Integer.class));

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/deliveries/cancel/" + delivery.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()
                );
    }

    @Test
    void testStatusDeliveryShippedSuccess() throws Exception{
        Delivery delivery = createDelivery();

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/deliveries/shipped/" + delivery.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Order nº" + delivery.getId() + " status changed to shipped successfully")
                );
    }

    @Test
    void testStatusDeliveryShippedDeliveryShippedException() throws Exception{
        Delivery delivery = EntityMock.delivery();
        delivery.setStatus(Delivery.Status.SHIPPED);
        deliveryRepository.save(delivery);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/deliveries/shipped/" + delivery.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Order nº" + delivery.getId() + " has already been shipped")
                );
    }

    @Test
    void testStatusDeliveryShippedDeliveryCanceledException() throws Exception{
        Delivery delivery = EntityMock.delivery();
        delivery.setStatus(Delivery.Status.CANCELED);
        deliveryRepository.save(delivery);

        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/deliveries/shipped/" + delivery.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Order nº" + delivery.getId() + " is canceled")
                );
    }

    @Test
    void testDeleteAllCanceledSuccess() throws Exception {
        Delivery delivery = EntityMock.delivery();
        delivery.setStatus(Delivery.Status.CANCELED);
        deliveryRepository.save(delivery);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/deliveries/delete/canceled")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()
                );
    }

    @Test
    void testDeleteAllCanceledEntityNotFoundException() throws Exception {
        Delivery delivery = createDelivery();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/deliveries/delete/canceled")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("No deliveries canceled found")
                );
    }
}
