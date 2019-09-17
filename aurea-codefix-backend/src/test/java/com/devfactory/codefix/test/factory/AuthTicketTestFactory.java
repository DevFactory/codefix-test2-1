package com.devfactory.codefix.test.factory;

import static com.devfactory.codefix.security.web.AuthInformation.builder;

import com.devfactory.codefix.customers.persistence.Customer;
import com.devfactory.codefix.security.web.AuthInformation;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AuthTicketTestFactory {

    public static final String AUTH_TOKEN = "token";
    public static final Customer CUSTOMER = new Customer();

    public static final AuthInformation AUTH_INFO = builder()
            .token(AUTH_TOKEN)
            .customer(CUSTOMER)
            .build();
}
