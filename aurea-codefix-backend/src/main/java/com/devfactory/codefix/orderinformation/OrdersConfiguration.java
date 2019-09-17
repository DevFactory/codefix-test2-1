package com.devfactory.codefix.orderinformation;

import com.devfactory.codefix.fixes.persistence.FixRepository;
import com.devfactory.codefix.issue.persistence.IssueRepository;
import com.devfactory.codefix.orderinformation.persistence.OrderInformationRepository;
import com.devfactory.codefix.orderinformation.service.IssueProcessor;
import com.devfactory.codefix.orderinformation.service.OrdersProcessor;
import com.devfactory.codefix.orderinformation.service.OrdersService;
import com.devfactory.codefix.orderinformation.web.OrdersMapper;
import com.devfactory.codefix.tickets.service.JiraService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class OrdersConfiguration {

    private final OrderInformationRepository orderRepository;
    private final IssueRepository issueRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Bean
    OrdersMapper ordersMapper() {
        return new OrdersMapper();
    }

    @Bean
    OrdersService ordersService() {
        return new OrdersService(orderRepository, issueRepository, eventPublisher);
    }

    @Bean
    IssueProcessor issueProcessor(FixRepository fixRepository, IssueRepository issueRepository,
            JiraService jiraService) {
        return new IssueProcessor(fixRepository, issueRepository, jiraService);
    }

    @Bean
    OrdersProcessor ordersProcessor(OrderInformationRepository orderRepository, IssueRepository issueRepository,
            IssueProcessor issueProcessor) {
        return new OrdersProcessor(orderRepository, issueRepository, issueProcessor);
    }
}
