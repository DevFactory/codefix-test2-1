package com.devfactory.codefix.customers.services;

import com.devfactory.codefix.customers.persistence.Customer;
import com.devfactory.codefix.customers.persistence.CustomerRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer getOrCreate(String name, String email) {
        return customerRepository.findByEmail(email)
                .orElseGet(() -> customerRepository.save(new Customer(name, email)));
    }
}
