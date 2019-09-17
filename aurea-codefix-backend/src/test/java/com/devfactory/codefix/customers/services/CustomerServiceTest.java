package com.devfactory.codefix.customers.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.devfactory.codefix.customers.persistence.Customer;
import com.devfactory.codefix.customers.persistence.CustomerRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private static final String EMAIL = "an email";
    private static final String NAME = "a name";

    @Mock
    private CustomerRepository customerRepository;

    private Customer customer = new Customer(NAME, EMAIL);

    @InjectMocks
    private CustomerService customerService;

    @Test
    void getByEmailWhenExist() {
        given(customerRepository.findByEmail(EMAIL)).willReturn(Optional.of(customer));

        Customer resultCustomer = customerService.getOrCreate(NAME, EMAIL);

        assertThat(resultCustomer).isEqualTo(customer);
        verify(customerRepository).findByEmail(EMAIL);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void getByEmailWhenNotExist() {
        given(customerRepository.findByEmail(EMAIL)).willReturn(Optional.empty());
        given(customerRepository.save(customer)).willReturn(customer);

        Customer resultCustomer = customerService.getOrCreate(NAME, EMAIL);

        assertThat(resultCustomer).isEqualTo(customer);
        verify(customerRepository).findByEmail(EMAIL);
    }
}
