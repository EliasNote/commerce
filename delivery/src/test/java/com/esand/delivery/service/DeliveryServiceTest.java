//package com.esand.delivery.service;
//
//import com.esand.delivery.client.products.ProductClient;
//import com.esand.delivery.entity.Delivery;
//import com.esand.delivery.entity.EntityMock;
//import com.esand.delivery.exception.ConnectionException;
//import com.esand.delivery.exception.EntityNotFoundException;
//import com.esand.delivery.exception.DeliveryCanceledException;
//import com.esand.delivery.exception.DeliveryShippedException;
//import com.esand.delivery.repository.DeliveryRepository;
//import com.esand.delivery.repository.pagination.DeliveryDtoPagination;
//import com.esand.delivery.web.dto.DeliveryResponseDto;
//import com.esand.delivery.web.dto.DeliverySaveDto;
//import com.esand.delivery.web.dto.PageableDto;
//import com.esand.delivery.web.mapper.DeliveryMapper;
//import feign.FeignException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
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
//        PageableDto response = deliveryService.findAll(page.getPageable());
//
//        assertNotNull(response);
//        assertNotNull(response.getContent());
//        assertEquals(1, response.getContent().size());
//
//        DeliveryDtoPagination delivery = (DeliveryDtoPagination) response.getContent().get(0);
//        assertEquals(page.getContent().get(0).getId(), delivery.getId());
//        assertEquals(page.getContent().get(0).getName(), delivery.getName());
//        assertEquals(page.getContent().get(0).getCpf(), delivery.getCpf());
//        assertEquals(page.getContent().get(0).getTitle(), delivery.getTitle());
//        assertEquals(page.getContent().get(0).getSku(), delivery.getSku());
//        assertEquals(page.getContent().get(0).getPrice(), delivery.getPrice());
//        assertEquals(page.getContent().get(0).getQuantity(), delivery.getQuantity());
//        assertEquals(page.getContent().get(0).getTotal(), delivery.getTotal());
//        assertEquals(page.getContent().get(0).getStatus(), delivery.getStatus());
//        assertEquals(page.getContent().get(0).getDate(), delivery.getDate());
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
//        assertThrows(EntityNotFoundException.class, () -> deliveryService.findAll(page.getPageable()));
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
//        assertEquals(response.getId(), deliveryResponseDto.getId());
//        assertEquals(response.getName(), deliveryResponseDto.getName());
//        assertEquals(response.getCpf(), deliveryResponseDto.getCpf());
//        assertEquals(response.getTitle(), deliveryResponseDto.getTitle());
//        assertEquals(response.getSku(), deliveryResponseDto.getSku());
//        assertEquals(response.getPrice(), deliveryResponseDto.getPrice());
//        assertEquals(response.getQuantity(), deliveryResponseDto.getQuantity());
//        assertEquals(response.getTotal(), deliveryResponseDto.getTotal());
//        assertEquals(response.getStatus(), deliveryResponseDto.getStatus());
//        assertEquals(response.getDate(), deliveryResponseDto.getDate());
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
//        when(deliveryRepository.findAllByStatus(any(Pageable.class), eq(Delivery.Status.SHIPPED))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findAllShipped(page.getPageable());
//
//        assertNotNull(response);
//        assertNotNull(response.getContent());
//        assertEquals(1, response.getContent().size());
//
//        DeliveryDtoPagination delivery = (DeliveryDtoPagination) response.getContent().get(0);
//        assertEquals(page.getContent().get(0).getId(), delivery.getId());
//        assertEquals(page.getContent().get(0).getName(), delivery.getName());
//        assertEquals(page.getContent().get(0).getCpf(), delivery.getCpf());
//        assertEquals(page.getContent().get(0).getTitle(), delivery.getTitle());
//        assertEquals(page.getContent().get(0).getSku(), delivery.getSku());
//        assertEquals(page.getContent().get(0).getPrice(), delivery.getPrice());
//        assertEquals(page.getContent().get(0).getQuantity(), delivery.getQuantity());
//        assertEquals(page.getContent().get(0).getTotal(), delivery.getTotal());
//        assertEquals(page.getContent().get(0).getStatus(), delivery.getStatus());
//        assertEquals(page.getContent().get(0).getDate(), delivery.getDate());
//    }
//
//    @Test
//    void testFindAllShippedEntityNotFoundException() {
//        Page<DeliveryDtoPagination> page = EntityMock.pageEmpty();
//        PageableDto pageableDto = EntityMock.pageableDtoEmpty();
//
//        when(deliveryRepository.findAllByStatus(any(Pageable.class), eq(Delivery.Status.SHIPPED))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        assertThrows(EntityNotFoundException.class, () -> deliveryService.findAllShipped(page.getPageable()));
//    }
//
//    @Test
//    void testFindAllProcessingSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findAllByStatus(any(Pageable.class), eq(Delivery.Status.PROCESSING))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findAllProcessing(page.getPageable());
//
//        assertNotNull(response);
//        assertNotNull(response.getContent());
//        assertEquals(1, response.getContent().size());
//
//        DeliveryDtoPagination delivery = (DeliveryDtoPagination) response.getContent().get(0);
//        assertEquals(page.getContent().get(0).getId(), delivery.getId());
//        assertEquals(page.getContent().get(0).getName(), delivery.getName());
//        assertEquals(page.getContent().get(0).getCpf(), delivery.getCpf());
//        assertEquals(page.getContent().get(0).getTitle(), delivery.getTitle());
//        assertEquals(page.getContent().get(0).getSku(), delivery.getSku());
//        assertEquals(page.getContent().get(0).getPrice(), delivery.getPrice());
//        assertEquals(page.getContent().get(0).getQuantity(), delivery.getQuantity());
//        assertEquals(page.getContent().get(0).getTotal(), delivery.getTotal());
//        assertEquals(page.getContent().get(0).getStatus(), delivery.getStatus());
//        assertEquals(page.getContent().get(0).getDate(), delivery.getDate());
//    }
//
//    @Test
//    void testFindAllProcessingEntityNotFoundException() {
//        Page<DeliveryDtoPagination> page = EntityMock.pageEmpty();
//        PageableDto pageableDto = EntityMock.pageableDtoEmpty();
//
//        when(deliveryRepository.findAllByStatus(any(Pageable.class), eq(Delivery.Status.PROCESSING))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        assertThrows(EntityNotFoundException.class, () -> deliveryService.findAllProcessing(page.getPageable()));
//    }
//
//    @Test
//    void testFindAllCanceledSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findAllByStatus(any(Pageable.class), eq(Delivery.Status.CANCELED))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findAllCanceled(page.getPageable());
//
//        assertNotNull(response);
//        assertNotNull(response.getContent());
//        assertEquals(1, response.getContent().size());
//
//        DeliveryDtoPagination delivery = (DeliveryDtoPagination) response.getContent().get(0);
//        assertEquals(page.getContent().get(0).getId(), delivery.getId());
//        assertEquals(page.getContent().get(0).getName(), delivery.getName());
//        assertEquals(page.getContent().get(0).getCpf(), delivery.getCpf());
//        assertEquals(page.getContent().get(0).getTitle(), delivery.getTitle());
//        assertEquals(page.getContent().get(0).getSku(), delivery.getSku());
//        assertEquals(page.getContent().get(0).getPrice(), delivery.getPrice());
//        assertEquals(page.getContent().get(0).getQuantity(), delivery.getQuantity());
//        assertEquals(page.getContent().get(0).getTotal(), delivery.getTotal());
//        assertEquals(page.getContent().get(0).getStatus(), delivery.getStatus());
//        assertEquals(page.getContent().get(0).getDate(), delivery.getDate());
//    }
//
//    @Test
//    void testFindAllCanceledEntityNotFoundException() {
//        Page<DeliveryDtoPagination> page = EntityMock.pageEmpty();
//        PageableDto pageableDto = EntityMock.pageableDtoEmpty();
//
//        when(deliveryRepository.findAllByStatus(any(Pageable.class), eq(Delivery.Status.CANCELED))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        assertThrows(EntityNotFoundException.class, () -> deliveryService.findAllCanceled(page.getPageable()));
//    }
//
//    @Test
//    void testFindDeliveryByDateBetweenSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findByDateBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findDeliveryByDate(LocalDate.now().minusDays(1).toString(), LocalDate.now().plusDays(1).toString(), page.getPageable());
//
//        assertNotNull(response.getContent());
//        assertEquals(1, response.getContent().size());
//
//        DeliveryDtoPagination delivery = (DeliveryDtoPagination) response.getContent().get(0);
//        assertEquals(page.getContent().get(0).getId(), delivery.getId());
//        assertEquals(page.getContent().get(0).getName(), delivery.getName());
//        assertEquals(page.getContent().get(0).getCpf(), delivery.getCpf());
//        assertEquals(page.getContent().get(0).getTitle(), delivery.getTitle());
//        assertEquals(page.getContent().get(0).getSku(), delivery.getSku());
//        assertEquals(page.getContent().get(0).getPrice(), delivery.getPrice());
//        assertEquals(page.getContent().get(0).getQuantity(), delivery.getQuantity());
//        assertEquals(page.getContent().get(0).getTotal(), delivery.getTotal());
//        assertEquals(page.getContent().get(0).getStatus(), delivery.getStatus());
//        assertEquals(page.getContent().get(0).getDate(), delivery.getDate());
//    }
//
//    @Test
//    void testFindDeliveryByDateAfterSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findByDateAfter(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findDeliveryByDate(LocalDate.now().minusDays(1).toString(), null, page.getPageable());
//
//        assertNotNull(response.getContent());
//        assertEquals(1, response.getContent().size());
//
//        DeliveryDtoPagination delivery = (DeliveryDtoPagination) response.getContent().get(0);
//        assertEquals(page.getContent().get(0).getId(), delivery.getId());
//        assertEquals(page.getContent().get(0).getName(), delivery.getName());
//        assertEquals(page.getContent().get(0).getCpf(), delivery.getCpf());
//        assertEquals(page.getContent().get(0).getTitle(), delivery.getTitle());
//        assertEquals(page.getContent().get(0).getSku(), delivery.getSku());
//        assertEquals(page.getContent().get(0).getPrice(), delivery.getPrice());
//        assertEquals(page.getContent().get(0).getQuantity(), delivery.getQuantity());
//        assertEquals(page.getContent().get(0).getTotal(), delivery.getTotal());
//        assertEquals(page.getContent().get(0).getStatus(), delivery.getStatus());
//        assertEquals(page.getContent().get(0).getDate(), delivery.getDate());
//    }
//
//    @Test
//    void testFindDeliveryByDateBeforeSuccess() {
//        Page<DeliveryDtoPagination> page = EntityMock.page();
//        PageableDto pageableDto = EntityMock.pageableDto();
//
//        when(deliveryRepository.findByDateBefore(any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        PageableDto response = deliveryService.findDeliveryByDate(null, LocalDate.now().plusDays(1).toString(), page.getPageable());
//
//        assertNotNull(response.getContent());
//        assertEquals(1, response.getContent().size());
//
//        DeliveryDtoPagination delivery = (DeliveryDtoPagination) response.getContent().get(0);
//        assertEquals(page.getContent().get(0).getId(), delivery.getId());
//        assertEquals(page.getContent().get(0).getName(), delivery.getName());
//        assertEquals(page.getContent().get(0).getCpf(), delivery.getCpf());
//        assertEquals(page.getContent().get(0).getTitle(), delivery.getTitle());
//        assertEquals(page.getContent().get(0).getSku(), delivery.getSku());
//        assertEquals(page.getContent().get(0).getPrice(), delivery.getPrice());
//        assertEquals(page.getContent().get(0).getQuantity(), delivery.getQuantity());
//        assertEquals(page.getContent().get(0).getTotal(), delivery.getTotal());
//        assertEquals(page.getContent().get(0).getStatus(), delivery.getStatus());
//        assertEquals(page.getContent().get(0).getDate(), delivery.getDate());
//    }
//
//    @Test
//    void testFindDeliveryByDateNoDateParametersProvided() {
//        assertThrows(EntityNotFoundException.class, () -> deliveryService.findDeliveryByDate(null, null, EntityMock.page().getPageable()));
//    }
//
//    @Test
//    void testFindDeliveryByDateEntityNotFoundException() {
//        Page<DeliveryDtoPagination> page = EntityMock.pageEmpty();
//        PageableDto pageableDto = EntityMock.pageableDtoEmpty();
//
//        when(deliveryRepository.findByDateBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class))).thenReturn(page);
//        when(deliveryMapper.toPageableDto(any(Page.class))).thenReturn(pageableDto);
//
//        assertThrows(EntityNotFoundException.class, () -> deliveryService.findDeliveryByDate(LocalDate.now().minusDays(1).toString(), LocalDate.now().plusDays(1).toString(), page.getPageable()));
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
//        FeignException.ServiceUnavailable feignException = mock(FeignException.ServiceUnavailable.class);
//        when(deliveryRepository.findById(any(Long.class))).thenReturn(Optional.of(delivery));
//        doThrow(feignException).when(productClient).checkStatus();
//
//        assertThrows(ConnectionException.class, () -> deliveryService.cancel(delivery.getId()));
//    }
//
//    @Test
//    void testCancelOrderByIdFeignEntityNotFoundException() {
//        Delivery delivery = EntityMock.delivery();
//
//        FeignException.NotFound feignException = mock(FeignException.NotFound.class);
//        when(deliveryRepository.findById(any(Long.class))).thenReturn(Optional.of(delivery));
//        doNothing().when(productClient).checkStatus();
//        doThrow(feignException).when(productClient).addProductQuantityBySku(any(String.class), any(Integer.class));
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