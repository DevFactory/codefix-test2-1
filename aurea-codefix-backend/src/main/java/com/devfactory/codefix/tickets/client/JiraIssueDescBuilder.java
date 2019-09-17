package com.devfactory.codefix.tickets.client;

import com.devfactory.codefix.issue.persistence.Issue;
import com.devfactory.codefix.orderinformation.model.Location;
import com.devfactory.codefix.orderinformation.model.OrderIssue;
import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class JiraIssueDescBuilder {

    private static final String ORDER_ID_LABEL = "Order Id: ";

    private final ObjectMapper mapper;

    String getIssueDescription(OrderInformation order, Issue issue) {
        return ORDER_ID_LABEL + order.getId() + "\n"
                + getOrderAsString(getIssueOrder(issue));
    }

    @SneakyThrows
    private String getOrderAsString(OrderIssue order) {
        return mapper.writer().withDefaultPrettyPrinter().writeValueAsString(order);
    }

    private OrderIssue getIssueOrder(Issue issue) {
        return OrderIssue.builder()
                .issueId(issue.getId())
                .issueType(issue.getIssueType().getDescription())
                .location(Location.builder()
                        .filePath(issue.getFilePath())
                        .commitSha(issue.getAnalysisRequest().getRevision())
                        .startLine(issue.getLineNumber())
                        .endLine(issue.getLineNumber())
                        .forkUrl(issue.getRepository().getForkUrl())
                        .branch(issue.getRepository().getBranch())
                        .build())
                .build();
    }
}
