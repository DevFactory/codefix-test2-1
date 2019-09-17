package com.devfactory.codefix.aline.event;

import com.devfactory.codefix.orderinformation.persistence.OrderInformation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Data
@Accessors(fluent = true)
public class OrderPendingEvent {

    private final OrderInformation order;
}
