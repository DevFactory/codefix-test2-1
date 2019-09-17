package com.devfactory.codefix.security.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.customers.persistence.Customer;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class UserInfoWrapperTest {

    private final Map<String, Object> userInfo = new HashMap<>();
    private final Customer customer = new Customer();

    private UserInfoWrapper testInstance = new UserInfoWrapper(userInfo, customer);

    @Test
    void constructorWhenNotNullUserInfo() {
        assertThat(new UserInfoWrapper(null, customer).getUserInfo()).isNull();
    }

    @Test
    void getUserInfo() {
        assertThat(testInstance.getUserInfo()).isEqualTo(userInfo);
    }

    @Test
    void getCustomer() {
        assertThat(testInstance.getCustomer()).isEqualTo(customer);
    }
}
