package com.esand.delivery.config;

import com.esand.delivery.service.DeliveryService;
import com.esand.delivery.web.dto.DeliverySaveDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class Listener {

    private final DeliveryService deliveryService;

    @KafkaListener(topics = "Orders", groupId = "result-group", containerFactory = "jsonContainerFactory")
    public void consumer(DeliverySaveDto dto) {
        try {
            deliveryService.save(dto);
            log.info("Order nº {} successfully saved in the database", dto.getId());
        } catch (Exception e) {
            log.error("Error saving order nº {} in the database:", dto.getId(), e);
        }
    }
}
