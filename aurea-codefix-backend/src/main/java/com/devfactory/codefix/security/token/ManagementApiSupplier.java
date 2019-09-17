package com.devfactory.codefix.security.token;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.exception.Auth0Exception;
import com.devfactory.codefix.security.SecurityProperties;
import com.devfactory.codefix.security.exception.ManagementApiException;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ManagementApiSupplier implements Supplier<ManagementAPI> {

    static final String AUTH_API = "https://devfactory.auth0.com/api/v2/";

    private final SecurityProperties props;
    private final AuthAPI authApi;

    @Override
    public ManagementAPI get() {
        try {
            return new ManagementAPI(props.getDomain(), authApi.requestToken(AUTH_API).execute().getAccessToken());
        } catch (Auth0Exception authException) {
            throw new ManagementApiException(authException);
        }
    }
}
