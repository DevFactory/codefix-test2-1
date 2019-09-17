package com.devfactory.codefix.orderinformation.service;

import static com.devfactory.codefix.orderinformation.persistence.OrderStatus.CONFIRMED;
import static com.devfactory.codefix.orderinformation.persistence.OrderStatus.IN_PROCESS;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.devfactory.codefix.customers.persistence.Customer;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.issue.persistence.IssueRepository;
import com.devfactory.codefix.orderinformation.exception.NotActiveOrderException;
import com.devfactory.codefix.orderinformation.exception.NotIssuesAvailableException;
import com.devfactory.codefix.orderinformation.exception.OrderAlreadyExistException;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.devfactory.codefix.orderinformation.persistence.OrderInformationRepository;
import com.devfactory.codefix.orderinformation.persistence.OrderStatus;
import com.devfactory.codefix.security.web.AuthInformation;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class OrdersServiceTest {

    @Mock
    private OrderInformationRepository orderRepository;

    @Mock
    private IssueRepository issueRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private Customer customer;

    @Mock
    private Issue issue;

    @Mock
    private AuthInformation authInfo;

    @Mock
    private OrderInformation order;

    private OrdersService testInstance;

    @BeforeEach
    void beforeEach() {
        testInstance = new OrdersService(orderRepository, issueRepository, eventPublisher);
    }

    @Test
    void getActive() {
        given(orderRepository.findByStatusInAndCustomer(asList(IN_PROCESS, CONFIRMED), customer)).willReturn(of(order));

        assertThat(testInstance.getActive(customer)).isEqualTo(order);
    }

    @Test
    void getActiveWhenNotExist() {
        given(orderRepository.findByStatusInAndCustomer(asList(IN_PROCESS, CONFIRMED), customer))
                .willReturn(Optional.empty());

        assertThrows(NotActiveOrderException.class, () -> testInstance.getActive(customer));
    }

    @Test
    void submit() {
        given(authInfo.getCustomer()).willReturn(customer);
        given(orderRepository.existsByStatusInAndCustomer(asList(IN_PROCESS, CONFIRMED), customer)).willReturn(false);
        given(issueRepository.existsByOrderInformationIsNullAndRepositoryCustomer(customer)).willReturn(false);

        assertThrows(NotIssuesAvailableException.class, () -> testInstance.submit(authInfo));
    }

    @Test
    void submitWhenAlreadyExist() {
        given(orderRepository.existsByStatusInAndCustomer(asList(IN_PROCESS, CONFIRMED), customer)).willReturn(true);
        given(authInfo.getCustomer()).willReturn(customer);

        assertThrows(OrderAlreadyExistException.class, () -> testInstance.submit(authInfo));
    }

    @Test
    void submitWhenAlreadyNotIssues() {
        given(authInfo.getCustomer()).willReturn(customer);
        given(orderRepository.existsByStatusInAndCustomer(asList(IN_PROCESS, CONFIRMED), customer)).willReturn(false);
        given(issueRepository.existsByOrderInformationIsNullAndRepositoryCustomer(customer)).willReturn(true);
        given(issueRepository.findTop100ByOrderInformationIsNullAndRepositoryCustomer(customer))
                .willReturn(singletonList(issue));
        given(orderRepository.save(any(OrderInformation.class))).willAnswer(returnsFirstArg());

        OrderInformation result = testInstance.submit(authInfo);

        assertThat(result.getIssues()).containsExactly(issue);
        assertThat(result.getStatus()).isEqualTo(OrderStatus.IN_PROCESS);
        assertThat(result.getStartDate()).isCloseTo(LocalDateTime.now(), within(10, SECONDS));
        assertThat(result.getDueDate()).isCloseTo(LocalDateTime.now().plusDays(30), within(10, SECONDS));
        assertThat(result.getIssuesFixCycle()).isEqualTo(0);
    }
}
