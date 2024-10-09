//package com.esand.delivery.service;
//
//import com.esand.delivery.client.products.ProductClient;
//import com.esand.delivery.entity.Delivery;
//import com.esand.delivery.entity.EntityMock;
//import com.esand.delivery.exception.ConnectionException;
//import com.esand.delivery.exception.EntityNotFoundException;
//import com.esand.delivery.exception.DeliveryCanceledException;
//import com.esand.delivery.exception.DeliveryShippedException;
//import com.esand.delivery.repository.delivery.DeliveryRepository;
//import com.esand.delivery.repository.pagination.DeliveryDtoPagination;
//import com.esand.delivery.web.dto.DeliveryResponseDto;
//import com.esand.delivery.web.dto.DeliverySaveDto;
//import com.esand.delivery.web.dto.PageableDto;
//import com.esand.delivery.web.mapper.DeliveryMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.HttpServerErrorException;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//class DeliveryServiceTest {
//
//    @Mock
//    private DeliveryRepository deliveryRepository;
//
//    @Mock
//    private DeliveryMapper deliveryMapper;
//
//    @Mock
//    private ProductClient productClient;
//
//    @InjectMocks
//    private DeliveryService deliveryService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    void verifyResult(DeliveryDtoPagination response, DeliveryResponseDto responseDto, Delivery delivery) {
//        if (response != null) {
//            assertNotNull(response);
//            assertEquals(delivery.getId(), response.getId());
//            assertEquals(delivery.getName(), response.getName());
//            assertEquals(delivery.getCpf(), response.getCpf());
//            assertEquals(delivery.getTitle(), response.getTitle());
//            assertEquals(delivery.getSku(), response.getSku());
//            assertEquals(delivery.getPrice(), response.getPrice());
//            assertEquals(delivery.getQuantity(), response.getQuantity());
//            assertEquals(delivery.getTotal(), response.getTotal());
//            assertEquals(delivery.getStatus().toString(), response.getStatus());
//            assertNotNull(response.getDate());
//        } else {
//            assertNotNull(responseDto);
//            assertEquals(delivery.getId(), responseDto.getId());
//            assertEquals(delivery.getName(), responseDto.getName());
//            assertEquals(delivery.getCpf(), responseDto.getCpf());
//            assertEquals(delivery.getTitle(), responseDto.getTitle());
//            assertEquals(delivery.getSku(), responseDto.getSku());
//            assertEquals(delivery.getPrice(), responseDto.getPrice());
//            assertEquals(delivery.getQuantity(), responseDto.getQuantity());
//            assertEquals(delivery.getTotal(), responseDto.getTotal());
//            assertEquals(delivery.getStatus().toString(), responseDto.getStatus());
//            assertNotNull(responseDto.getDate());
//        }
//    }
//
//    @Test
//    void testSaveDeliverySuccess() {
//        DeliverySaveDto deliverySaveDto = EntityMock.saveDto();
//        Delivery delivery = EntityMock.delivery();
//
//        when(deliveryMapper.toDelivery(any(DeliverySaveDto.class))).thenReturn(delivery);
//
//        deliveryService.save(deliverySaveDto);
//    }
//
//    @Test
//    void testFindAllSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findAll(null, null, page.getPageable());
//
//        verifyResult((DeliveryDtoPagination) response.getContent().get(0), null, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindAllByDateBetweenSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findAllByDateBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findAll(LocalDate.now().minusDays(1).toString(), LocalDate.now().plusDays(1).toString(), page.getPageable());
//
//        verifyResult((DeliveryDtoPagination) response.getContent().get(0), null, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindAllByDateAfterSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findAllByDateAfter(any(LocalDateTime.class),any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findAll(LocalDate.now().minusDays(1).toString(), null, page.getPageable());
//
//        verifyResult((DeliveryDtoPagination) response.getContent().get(0), null, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindAllByDateBeforeSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findAllByDateBefore(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findAll(null, LocalDate.now().plusDays(1).toString(), page.getPageable());
//
//        verifyResult((DeliveryDtoPagination) response.getContent().get(0), null, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindAllEntityNotFoundException() {
//        Page<DeliveryDtoPagination> page = EntityMock.pageEmpty();
//        PageableDto pageableDto = EntityMock.pageableDtoEmpty();
//
//        when(deliveryRepository.findAllPageable(any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        assertThrows(EntityNotFoundException.class, () -> deliveryService.findAll(null, null,page.getPageable()));
//    }
//
//    @Test
//    void testFindByIdSuccess() {
//        Delivery delivery = EntityMock.delivery();
//        DeliveryResponseDto deliveryResponseDto = EntityMock.responseDto();
//
//        when(deliveryRepository.findById(any(Long.class))).thenReturn(Optional.of(delivery));
//        when(deliveryMapper.toDto(any(Delivery.class))).thenReturn(deliveryResponseDto);
//
//        DeliveryResponseDto response = deliveryService.findById(delivery.getId());
//
//        verifyResult(null, response, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindByIdEntityNotFoundException() {
//        when(deliveryRepository.findById(any(Long.class))).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> deliveryService.findById(1L));
//    }
//
//    @Test
//    void testFindAllShippedSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findAllByStatus(eq(Delivery.Status.SHIPPED), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findAllShipped(null, null, page.getPageable());
//
//        verifyResult((DeliveryDtoPagination) response.getContent().get(0), null, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindAllShippedByDateBetweenSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findAllByStatusAndDateBetween(eq(Delivery.Status.SHIPPED), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findAllShipped(LocalDate.now().minusDays(1).toString(), LocalDate.now().plusDays(1).toString(), page.getPageable());
//
//        verifyResult((DeliveryDtoPagination) response.getContent().get(0), null, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindAllShippedByDateAfterSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findAllByStatusAndDateAfter(eq(Delivery.Status.SHIPPED), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findAllShipped(LocalDate.now().minusDays(1).toString(), null, page.getPageable());
//
//        verifyResult((DeliveryDtoPagination) response.getContent().get(0), null, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindAllShippedByDateBeforeSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findAllByStatusAndDateBefore(eq(Delivery.Status.SHIPPED), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findAllShipped(null, LocalDate.now().plusDays(1).toString(), page.getPageable());
//
//        verifyResult((DeliveryDtoPagination) response.getContent().get(0), null, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindAllShippedEntityNotFoundException() {
//        Page<DeliveryDtoPagination> page = EntityMock.pageEmpty();
//        PageableDto pageableDto = EntityMock.pageableDtoEmpty();
//
//        when(deliveryRepository.findAllByStatus(eq(Delivery.Status.SHIPPED), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        assertThrows(EntityNotFoundException.class, () -> deliveryService.findAllShipped(null, null, page.getPageable()));
//    }
//
//    @Test
//    void testFindAllProcessingSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findAllByStatus(eq(Delivery.Status.PROCESSING), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findAllProcessing(null, null, page.getPageable());
//
//        verifyResult((DeliveryDtoPagination) response.getContent().get(0), null, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindAllProcessingByDateBetweenSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findAllByStatusAndDateBetween(eq(Delivery.Status.PROCESSING), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findAllProcessing(LocalDate.now().minusDays(1).toString(), LocalDate.now().plusDays(1).toString(), page.getPageable());
//
//        verifyResult((DeliveryDtoPagination) response.getContent().get(0), null, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindAllProcessingByDateAfterSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findAllByStatusAndDateAfter(eq(Delivery.Status.PROCESSING), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findAllProcessing(LocalDate.now().minusDays(1).toString(), null, page.getPageable());
//
//        verifyResult((DeliveryDtoPagination) response.getContent().get(0), null, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindAllProcessingByDateBeforeSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findAllByStatusAndDateBefore(eq(Delivery.Status.PROCESSING), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findAllProcessing(null, LocalDate.now().plusDays(1).toString(), page.getPageable());
//
//        verifyResult((DeliveryDtoPagination) response.getContent().get(0), null, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindAllProcessingEntityNotFoundException() {
//        Page<DeliveryDtoPagination> page = EntityMock.pageEmpty();
//        PageableDto pageableDto = EntityMock.pageableDtoEmpty();
//
//        when(deliveryRepository.findAllByStatus(eq(Delivery.Status.PROCESSING), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        assertThrows(EntityNotFoundException.class, () -> deliveryService.findAllProcessing(null, null, page.getPageable()));
//    }
//
//    @Test
//    void testFindAllCanceledSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findAllByStatus(eq(Delivery.Status.CANCELED), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findAllCanceled(null, null, page.getPageable());
//
//        verifyResult((DeliveryDtoPagination) response.getContent().get(0), null, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindAllCanceledByDateBetweenSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findAllByStatusAndDateBetween(eq(Delivery.Status.CANCELED), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findAllCanceled(LocalDate.now().minusDays(1).toString(), LocalDate.now().plusDays(1).toString(), page.getPageable());
//
//        verifyResult((DeliveryDtoPagination) response.getContent().get(0), null, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindAllCanceledByDateAfterSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findAllByStatusAndDateAfter(eq(Delivery.Status.CANCELED), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findAllCanceled(LocalDate.now().minusDays(1).toString(), null, page.getPageable());
//
//        verifyResult((DeliveryDtoPagination) response.getContent().get(0), null, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindAllCanceledByDateBeforeSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findAllByStatusAndDateBefore(eq(Delivery.Status.CANCELED), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findAllCanceled(null, LocalDate.now().plusDays(1).toString(), page.getPageable());
//
//        verifyResult((DeliveryDtoPagination) response.getContent().get(0), null, EntityMock.delivery());
//    }
//
//    @Test
//    void testFindAllCanceledEntityNotFoundException() {
//        Page<DeliveryDtoPagination> page = EntityMock.pageEmpty();
//        PageableDto pageableDto = EntityMock.pageableDtoEmpty();
//
//        when(deliveryRepository.findAllByStatus(eq(Delivery.Status.CANCELED), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        assertThrows(EntityNotFoundException.class, () -> deliveryService.findAllCanceled(null, null, page.getPageable()));
//    }
//
//    @Test
//    void testCancelOrderByIdSuccess() {
//        Delivery delivery = EntityMock.delivery();
//
//        when(deliveryRepository.findById(any(Long.class))).thenReturn(Optional.of(delivery));
//        doNothing().when(productClient).checkStatus();
//        doNothing().when(productClient).addProductQuantityBySku(any(String.class), any(Integer.class));
//
//        String response = deliveryService.cancel(delivery.getId());
//
//        assertEquals(response, "Order nº" + delivery.getId() + " status changed to canceled successfully");
//    }
//
//    @Test
//    void testCancelOrderByIdOrderEntityNotFoundException() {
//        Delivery delivery = EntityMock.delivery();
//
//        when(deliveryRepository.findById(any(Long.class))).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> deliveryService.cancel(delivery.getId()));
//    }
//
//    @Test
//    void testCancelOrderByIdOrderCanceledException() {
//        Delivery delivery = EntityMock.delivery();
//        delivery.setStatus(Delivery.Status.CANCELED);
//
//        when(deliveryRepository.findById(any(Long.class))).thenReturn(Optional.of(delivery));
//
//        assertThrows(DeliveryCanceledException.class, () -> deliveryService.cancel(delivery.getId()));
//    }
//
//    @Test
//    void testCancelOrderByIdProductFeignConnectionException() {
//        Delivery delivery = EntityMock.delivery();
//
//        HttpServerErrorException.ServiceUnavailable connectionException = mock(HttpServerErrorException.ServiceUnavailable.class);
//        when(deliveryRepository.findById(any(Long.class))).thenReturn(Optional.of(delivery));
//        doThrow(connectionException).when(productClient).checkStatus();
//
//        assertThrows(ConnectionException.class, () -> deliveryService.cancel(delivery.getId()));
//    }
//
//    @Test
//    void testCancelOrderByIdFeignEntityNotFoundException() {
//        Delivery delivery = EntityMock.delivery();
//
//        HttpClientErrorException.NotFound notFoundException = mock(HttpClientErrorException.NotFound.class);
//        when(deliveryRepository.findById(any(Long.class))).thenReturn(Optional.of(delivery));
//        doNothing().when(productClient).checkStatus();
//        doThrow(notFoundException).when(productClient).addProductQuantityBySku(any(String.class), any(Integer.class));
//
//        assertThrows(EntityNotFoundException.class, () -> deliveryService.cancel(delivery.getId()));
//    }
//
//    @Test
//    void testStatusShippedSuccess() {
//        Delivery delivery = EntityMock.delivery();
//
//        when(deliveryRepository.findById(any(Long.class))).thenReturn(Optional.of(delivery));
//
//        String response = deliveryService.statusShipped(delivery.getId());
//
//        assertEquals(response, "Order nº" + delivery.getId() + " status changed to shipped successfully");
//    }
//
//    @Test
//    void testStatusShippedEntityNotFoundException() {
//        Delivery delivery = EntityMock.delivery();
//
//        when(deliveryRepository.findById(any(Long.class))).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> deliveryService.statusShipped(delivery.getId()));
//    }
//
//    @Test
//    void testStatusShippedOrderShippedException() {
//        Delivery delivery = EntityMock.delivery();
//        delivery.setStatus(Delivery.Status.SHIPPED);
//
//        when(deliveryRepository.findById(any(Long.class))).thenReturn(Optional.of(delivery));
//
//        assertThrows(DeliveryShippedException.class, () -> deliveryService.statusShipped(delivery.getId()));
//    }
//
//    @Test
//    void testStatusShippedOrderCanceledException() {
//        Delivery delivery = EntityMock.delivery();
//        delivery.setStatus(Delivery.Status.CANCELED);
//
//        when(deliveryRepository.findById(any(Long.class))).thenReturn(Optional.of(delivery));
//
//        assertThrows(DeliveryCanceledException.class, () -> deliveryService.statusShipped(delivery.getId()));
//    }
//
//    @Test
//    void testDeleteAllCanceledSuccess() {
//        when(deliveryRepository.existsByStatus(eq(Delivery.Status.CANCELED))).thenReturn(true);
//
//        deliveryService.deleteAllCanceled();
//    }
//
//    @Test
//    void testDeleteAllCanceledEntityNotFoundException() {
//        when(deliveryRepository.existsByStatus(eq(Delivery.Status.CANCELED))).thenReturn(false);
//
//        assertThrows(EntityNotFoundException.class, () -> deliveryService.deleteAllCanceled());
//    }
//}