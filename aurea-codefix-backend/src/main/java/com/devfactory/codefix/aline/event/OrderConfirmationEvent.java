package com.devfactory.codefix.aline.event;

import com.devfactory.codefix.tickets.dto.AssemblyLineOrderConfirmation;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderConfirmationEvent {

    private AssemblyLineOrderConfirmation orderConfirmation;
}
