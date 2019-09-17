package com.devfactory.codefix.orderinformation.events;

import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import com.devfactory.codefix.security.web.AuthInformation;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderSubmittedEvent {

    private final OrderInformation order;
    private final AuthInformation authInformation;
}
