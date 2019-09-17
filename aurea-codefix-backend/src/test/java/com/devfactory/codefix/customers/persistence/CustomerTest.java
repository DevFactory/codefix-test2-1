package com.devfactory.codefix.customers.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CustomerTest {

    private static final String NAME = "jhon";
    private static final String EMAIL = "jhon@email.com";

    @Test
    void constructor() {
        Customer customer = new Customer(NAME, EMAIL);

        assertThat(customer.getEmail()).isEqualTo(EMAIL);
        assertThat(customer.getName()).isEqualTo(NAME);
    }
}
