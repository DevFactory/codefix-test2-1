package com.devfactory.codefix.security.web;

import static com.google.common.base.Suppliers.memoize;

import com.devfactory.codefix.customers.persistence.Customer;
import java.util.function.Supplier;
import lombok.Builder;
import lombok.Getter;

/**
 * Contains auth0 extracted user information.
 */
@Builder
public class AuthInformation {

    @Getter
    private final String token;

    @Getter
    private final Customer customer;

    private final com.google.common.base.Supplier<String> tokenSupplier;

    public String getAccessToken() {
        return tokenSupplier.get();
    }

    public static class AuthInformationBuilder {

        public AuthInformationBuilder accessToken(Supplier<String> tokenSupplier) {
            this.tokenSupplier = memoize(tokenSupplier::get);
            return this;
        }
    }
}
