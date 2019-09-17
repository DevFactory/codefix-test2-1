package com.devfactory.codefix.orderinformation.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;

@Data
@Builder
public class OrderIssue {

    private long issueId;
    private String issueType;

    @Singular
    private List<Location> locations;

}
