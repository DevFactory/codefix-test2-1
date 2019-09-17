package com.devfactory.codefix.aline.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.devfactory.codefix.aline.AssemblyLineProperties;
import com.devfactory.codefix.aline.mapper.AssemblyLineOrderMapper;
import com.devfactory.codefix.orderinformation.events.OrderSubmittedEvent;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.devfactory.codefix.security.web.AuthInformation;
import com.devfactory.codefix.tickets.dto.AssemblyLineOrder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;

@ExtendWith(MockitoExtension.class)
class AssemblyLineOrderJmsSenderTest {

    private static final String DESTINATION = "d35t1n4t10n";

    @Mock
    private JmsTemplate jmsTemplate;

    @Mock
    private AssemblyLineOrderMapper orderMapper;

    @Mock
    private AuthInformation authInfo;

    @Mock
    private AssemblyLineProperties properties;

    @InjectMocks
    private AssemblyLineOrderJmsSender testInstance;

    @Test
    void orderIsConvertedAndSent() {
        when(properties.getOrderQueue()).thenReturn(DESTINATION);

        OrderInformation cfOrder = new OrderInformation();
        AssemblyLineOrder alOrder = AssemblyLineOrder.builder().build();

        when(orderMapper.toDto(cfOrder)).thenReturn(alOrder);

        testInstance.sendOrder(new OrderSubmittedEvent(cfOrder, authInfo));

        verify(orderMapper).toDto(eq(cfOrder));
        verify(jmsTemplate).convertAndSend(eq(DESTINATION), eq(alOrder));
    }

    @Test
    void orderIsNotSentWhenMapperFails() {
        OrderInformation cfOrder = new OrderInformation();
        when(orderMapper.toDto(cfOrder)).thenThrow(new IllegalArgumentException("Some exception"));

        assertThrows(IllegalArgumentException.class,
                () -> testInstance.sendOrder(new OrderSubmittedEvent(cfOrder, authInfo)));
        verify(orderMapper).toDto(cfOrder);
        verifyZeroInteractions(jmsTemplate);
    }
}
