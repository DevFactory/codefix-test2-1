package com.devfactory.codefix.aline.service;

import com.devfactory.codefix.aline.AssemblyLineProperties;
import com.devfactory.codefix.aline.mapper.AssemblyLineOrderMapper;
import com.devfactory.codefix.orderinformation.events.OrderSubmittedEvent;
import com.devfactory.codefix.tickets.dto.AssemblyLineOrder;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Async;

@AllArgsConstructor
public class AssemblyLineOrderJmsSender {

    private final AssemblyLineProperties properties;
    private final JmsTemplate jmsTemplate;
    private final AssemblyLineOrderMapper orderMapper;

    @Async
    @EventListener
    public void sendOrder(OrderSubmittedEvent event) {
        AssemblyLineOrder payload = orderMapper.toDto(event.getOrder());
        jmsTemplate.convertAndSend(properties.getOrderQueue(), payload);
    }

}
