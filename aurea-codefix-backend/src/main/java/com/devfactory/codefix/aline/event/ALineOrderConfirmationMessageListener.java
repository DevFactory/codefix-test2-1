package com.devfactory.codefix.aline.event;

import com.devfactory.codefix.tickets.dto.AssemblyLineOrderConfirmation;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Slf4j
public class ALineOrderConfirmationMessageListener {

    private static final String ACCEPTED_STATUS = "accepted";

    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    @JmsListener(destination = "${app.integration.assemblyline.confirmationsQueue}")
    public void handleConfirmationMessage(String message) {
        AssemblyLineOrderConfirmation orderConfirmation = getOrderConfirmation(message);
        if (ACCEPTED_STATUS.equalsIgnoreCase(orderConfirmation.getStatus())) {
            eventPublisher.publishEvent(new OrderConfirmationEvent(orderConfirmation));
        }
    }

    @SneakyThrows
    private AssemblyLineOrderConfirmation getOrderConfirmation(String message) {
        return objectMapper.readValue(message, AssemblyLineOrderConfirmation.class);
    }

}
