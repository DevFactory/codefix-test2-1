package com.devfactory.codefix.orderinformation.web.dto;

import static java.util.Collections.unmodifiableList;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class OrderDto {

    private final LocalDateTime startDate;
    private final LocalDateTime dueDate;
    private final List<OrderIssueDto> issues;

    public OrderDto(LocalDateTime startDate, LocalDateTime dueDate, List<OrderIssueDto> issues) {
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.issues = unmodifiableList(issues);
    }
}
