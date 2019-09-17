package com.devfactory.codefix.orderinformation.service;

import static com.devfactory.codefix.orderinformation.persistence.OrderStatus.CONFIRMED;
import static com.devfactory.codefix.orderinformation.persistence.OrderStatus.IN_PROCESS;
import static java.util.Arrays.asList;

import com.devfactory.codefix.customers.persistence.Customer;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.persistence.IssueRepository;
import com.devfactory.codefix.orderinformation.events.OrderSubmittedEvent;
import com.devfactory.codefix.orderinformation.exception.NotActiveOrderException;
import com.devfactory.codefix.orderinformation.exception.NotIssuesAvailableException;
import com.devfactory.codefix.orderinformation.exception.OrderAlreadyExistException;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.devfactory.codefix.orderinformation.persistence.OrderInformationRepository;
import com.devfactory.codefix.security.web.AuthInformation;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
public class OrdersService {

    private static final long DEFAULT_FIX_CYCLE = 0L;
    private static final int CYCLE_DURATION = 30;

    private final OrderInformationRepository orderRepository;
    private final IssueRepository issueRepository;
    private final ApplicationEventPublisher eventPublisher;

    public OrderInformation getActive(Customer customer) {
        return orderRepository
                .findByStatusInAndCustomer(asList(IN_PROCESS, CONFIRMED), customer)
                .orElseThrow(NotActiveOrderException::new);
    }

    @Transactional
    public OrderInformation submit(AuthInformation authInfo) {
        Customer customer = authInfo.getCustomer();

        if (orderRepository.existsByStatusInAndCustomer(asList(IN_PROCESS, CONFIRMED), customer)) {
            throw new OrderAlreadyExistException();
        }

        if (!issueRepository.existsByOrderInformationIsNullAndRepositoryCustomer(customer)) {
            throw new NotIssuesAvailableException();
        }

        OrderInformation newOrder = orderRepository.save(new OrderInformation()
                .setIssues(getIssues(customer))
                .setCustomer(customer)
                .setStatus(IN_PROCESS)
                .setStartDate(LocalDateTime.now())
                .setIssuesFixCycle(DEFAULT_FIX_CYCLE)
                .setDueDate(LocalDateTime.now().plusDays(CYCLE_DURATION)));
        eventPublisher.publishEvent(new OrderSubmittedEvent(newOrder, authInfo));
        return newOrder;
    }

    private Set<Issue> getIssues(Customer customer) {
        return new HashSet<>(issueRepository.findTop100ByOrderInformationIsNullAndRepositoryCustomer(customer));
    }
}
