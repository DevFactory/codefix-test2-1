package com.devfactory.codefix.customers;

import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.customers.persistence.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomersConfigTest {

    private CustomersConfig testInstance = new CustomersConfig();

    @Test
    void customerService(@Mock CustomerRepository customerRepository) {
        assertThat(testInstance.customerService(customerRepository)).isNotNull();
    }
}
