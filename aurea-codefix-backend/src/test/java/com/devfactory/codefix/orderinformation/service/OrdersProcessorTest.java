package com.devfactory.codefix.orderinformation.service;

import static java.util.Collections.singletonList;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.devfactory.codefix.aline.event.OrderConfirmationEvent;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.persistence.IssueRepository;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.devfactory.codefix.orderinformation.persistence.OrderInformationRepository;
import com.devfactory.codefix.orderinformation.persistence.OrderStatus;
import com.devfactory.codefix.tickets.dto.AssemblyLineOrderConfirmation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrdersProcessorTest {

    private static final long ORDER_ID = 77L;

    @Mock
    private OrderInformationRepository orderRepository;

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private OrderInformation order;

    @Mock
    private Issue issue;

    @Mock
    private IssueProcessor issueProcessor;

    @Mock
    private AssemblyLineOrderConfirmation orderConfirmation;

    @Mock
    private OrderConfirmationEvent confirmationEvent;

    private OrdersProcessor testInstance;

    @BeforeEach
    void beforeEach() {
        testInstance = new OrdersProcessor(orderRepository, issueRepository, issueProcessor);

        given(confirmationEvent.getOrderConfirmation()).willReturn(orderConfirmation);
        given(orderConfirmation.getOrderId()).willReturn(ORDER_ID);
        given(orderRepository.getById(ORDER_ID)).willReturn(order);
    }

    @Test
    void handleConfirmation() {
        given(issueRepository.findByOrderInformation(order)).willReturn(singletonList(issue));
        given(orderRepository.save(any(OrderInformation.class))).willAnswer(returnsFirstArg());
        given(order.getStatus()).willReturn(OrderStatus.IN_PROCESS);
        given(order.setStatus(OrderStatus.CONFIRMED)).willReturn(order);

        testInstance.handleConfirmation(confirmationEvent);
    }

    @Test
    void handleConfirmationWhenOrderNotInProcess() {
        given(order.getStatus()).willReturn(OrderStatus.CONFIRMED);

        testInstance.handleConfirmation(confirmationEvent);

        verifyZeroInteractions(issueProcessor);
    }
}
