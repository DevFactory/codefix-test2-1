package com.devfactory.codefix.orderinformation.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class OrderIssueDto {

    private long id;
    private String type;
    private String description;
    private String repository;
    private String branch;
    private OrderIssueStatus status;

    public enum OrderIssueStatus {
        IN_PROCESS,
        COMPLETED
    }
}
