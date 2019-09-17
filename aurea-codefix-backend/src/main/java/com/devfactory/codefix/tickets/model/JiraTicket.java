package com.devfactory.codefix.tickets.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JiraTicket {

    private final long id;
    private final String htmlUrl;
}
