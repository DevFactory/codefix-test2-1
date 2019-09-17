package com.devfactory.codefix.test.security;

import static org.springframework.util.ClassUtils.isAssignable;

import com.devfactory.codefix.customers.persistence.Customer;
import com.devfactory.codefix.security.web.AuthInformation;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class MockUserInfoResolver implements HandlerMethodArgumentResolver {

    public static final String TEST_TOKEN = "test123";
    private static final String TEST_USER = "user";
    private static final String TEST_EMAIL = "user@mail.com";
    public static final Customer TEST_CUSTOMER = new Customer().setEmail(TEST_EMAIL).setName(TEST_USER);
    public static final AuthInformation AUTH_INFO = AuthInformation.builder()
            .token(TEST_TOKEN)
            .customer(TEST_CUSTOMER)
            .build();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return isAssignable(parameter.getParameterType(), AuthInformation.class);
    }

    @Override
    public AuthInformation resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        return AUTH_INFO;
    }
}
