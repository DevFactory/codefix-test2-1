package com.devfactory.codefix.customers.notification.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class Email {

    private String from;
    private String destination;
    private String subject;
    private String content;
    private boolean isHtml;

}
