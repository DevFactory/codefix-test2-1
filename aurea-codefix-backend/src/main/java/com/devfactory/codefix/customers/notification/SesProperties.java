package com.devfactory.codefix.customers.notification;

import lombok.Data;

@Data
public class SesProperties {

    private String fromEmail;
    private String fromName;
    private String subject;
    private String content;

}
