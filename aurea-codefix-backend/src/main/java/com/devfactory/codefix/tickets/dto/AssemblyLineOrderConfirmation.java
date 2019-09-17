package com.devfactory.codefix.tickets.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AssemblyLineOrderConfirmation {

    private long orderId;
    private String status;
    private String failReason;
}
