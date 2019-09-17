package com.devfactory.codefix.customers;

import com.devfactory.codefix.customers.persistence.CustomerRepository;
import com.devfactory.codefix.customers.services.CustomerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomersConfig {

    @Bean
    CustomerService customerService(CustomerRepository customerRepository) {
        return new CustomerService(customerRepository);
    }
}
