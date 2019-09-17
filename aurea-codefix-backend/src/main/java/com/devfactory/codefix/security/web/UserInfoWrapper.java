package com.devfactory.codefix.security.web;

import com.devfactory.codefix.customers.persistence.Customer;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;

@Getter
final class UserInfoWrapper {

    private final Map<String, Object> userInfo;
    private final Customer customer;

    UserInfoWrapper(Map<String, Object> userInfo, Customer customer) {
        this.userInfo = userInfo != null ? Collections.unmodifiableMap(userInfo) : null;
        this.customer = customer;
    }
}
