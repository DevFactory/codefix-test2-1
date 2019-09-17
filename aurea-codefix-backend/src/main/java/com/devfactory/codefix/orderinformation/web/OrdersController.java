package com.devfactory.codefix.orderinformation.web;

import com.devfactory.codefix.orderinformation.service.OrdersService;
import com.devfactory.codefix.orderinformation.web.dto.OrderDto;
import com.devfactory.codefix.security.web.AuthInformation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrdersController {

    private final OrdersService ordersService;
    private final OrdersMapper ordersMapper;

    @PostMapping("/submit")
    public OrderDto submit(AuthInformation authInformation) {
        return ordersMapper.asOrderDto(ordersService.submit(authInformation));
    }

    @GetMapping("/active")
    public OrderDto getActive(AuthInformation authInformation) {
        return ordersMapper.asOrderDto(ordersService.getActive(authInformation.getCustomer()));
    }
}
