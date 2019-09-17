package com.devfactory.codefix.orderinformation.web;

import static com.devfactory.codefix.orderinformation.web.dto.OrderIssueDto.OrderIssueStatus.COMPLETED;
import static com.devfactory.codefix.orderinformation.web.dto.OrderIssueDto.OrderIssueStatus.IN_PROCESS;
import static java.util.stream.Collectors.toList;

import com.devfactory.codefix.fixes.persistence.Fix;
import com.devfactory.codefix.fixes.persistence.FixStatus;
import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.devfactory.codefix.orderinformation.web.dto.OrderDto;
import com.devfactory.codefix.orderinformation.web.dto.OrderIssueDto;
import com.devfactory.codefix.orderinformation.web.dto.OrderIssueDto.OrderIssueStatus;
import com.devfactory.codefix.repositories.persistence.CodefixRepository;
import java.util.List;
import java.util.Set;

public class OrdersMapper {

    OrderDto asOrderDto(OrderInformation order) {
        return new OrderDto(order.getStartDate(), order.getDueDate(), asIssueDtoList(order.getIssues()));
    }

    private List<OrderIssueDto> asIssueDtoList(Set<Issue> issues) {
        return issues.stream().map(this::asIssueDto).collect(toList());
    }

    private OrderIssueDto asIssueDto(Issue issue) {
        CodefixRepository repo = issue.getRepository();
        return OrderIssueDto.builder()
                .id(issue.getId())
                .type(issue.getIssueType().getDescription())
                .description(issue.getIssueDesc())
                .repository(repo.getUrl())
                .branch(repo.getBranch())
                .status(getStatus(issue.getFix()))
                .build();
    }

    private OrderIssueStatus getStatus(Fix fix) {
        return fix == null || fix.getStatus() == FixStatus.NONE ? IN_PROCESS : COMPLETED;
    }
}
