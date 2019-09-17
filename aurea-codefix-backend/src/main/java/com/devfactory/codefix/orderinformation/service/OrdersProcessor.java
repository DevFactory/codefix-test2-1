package com.devfactory.codefix.orderinformation.service;

import static java.util.stream.Collectors.toList;

import com.devfactory.codefix.aline.event.OrderConfirmationEvent;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.persistence.IssueRepository;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.devfactory.codefix.orderinformation.persistence.OrderInformationRepository;
import com.devfactory.codefix.orderinformation.persistence.OrderStatus;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@AllArgsConstructor
public class OrdersProcessor {

    private final OrderInformationRepository orderRepository;
    private final IssueRepository issueRepository;
    private final IssueProcessor issueProcessor;

    @Transactional
    @EventListener
    public void handleConfirmation(OrderConfirmationEvent event) {
        OrderInformation order = orderRepository.getById(event.getOrderConfirmation().getOrderId());
        if (order.getStatus() == OrderStatus.IN_PROCESS) {
            log.info("Confirming order '{}'", order.getId());
            getIssuesId(order).forEach(id -> issueProcessor.processIssue(order, id));
            orderRepository.save(order.setStatus(OrderStatus.CONFIRMED));
        }
    }

    private List<Long> getIssuesId(OrderInformation order) {
        return issueRepository.findByOrderInformation(order).stream().map(Issue::getId).collect(toList());
    }

}
