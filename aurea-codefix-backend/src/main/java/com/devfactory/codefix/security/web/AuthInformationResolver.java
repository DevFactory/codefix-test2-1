package com.devfactory.codefix.security.web;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.util.ClassUtils.isAssignable;

import com.auth0.client.auth.AuthAPI;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.mgmt.users.Identity;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.devfactory.codefix.customers.persistence.Customer;
import com.devfactory.codefix.customers.services.CustomerService;
import com.devfactory.codefix.security.exception.ManagementApiException;
import com.devfactory.codefix.security.exception.NoAccessTokenException;
import com.devfactory.codefix.security.token.ManagementApiSupplier;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.Striped;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import lombok.AllArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * {@link AuthInformation} resolver, uses auth0 api and obtain user information based on token information.
 */
@AllArgsConstructor
public class AuthInformationResolver implements HandlerMethodArgumentResolver {

    public static final String TOKEN_PREFIX = "Bearer ";

    private static final String NAME_FIELD = "name";
    private static final String EMAIL_FIELD = "email";
    private static final String SUB_FIELD = "sub";
    private static final String ACCESS_TOKEN = "github";
    private static final String JWT_PAYLOAD_CLAIM_SUB = "sub";

    private final CustomerService customerService;
    private final AuthAPI authApi;
    private final ManagementApiSupplier managementApiSupplier;
    private final Cache<String, UserInfoWrapper> userInfoCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();
    private final Striped<Lock> subBasedLock = Striped.lazyWeakLock(1);


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
        String token = getToken(webRequest);

        UserInfoWrapper userInfoWrapper = getUserInfo(token);

        final Map<String, Object> userInfo = userInfoWrapper.getUserInfo();

        return AuthInformation.builder()
                .token(token)
                .customer(userInfoWrapper.getCustomer())
                .accessToken(() -> getAccessToken((String) userInfo.get(SUB_FIELD)))
                .build();
    }

    private UserInfoWrapper getUserInfo(String token) throws Auth0Exception {
        Claim subClaim = JWT.decode(token).getClaim(JWT_PAYLOAD_CLAIM_SUB);

        if (!subClaim.isNull()) {
            String sub = subClaim.asString();
            Lock lock = subBasedLock.get(sub);
            try {
                lock.lock();
                return getUserInfo(sub, token);
            } finally {
                lock.unlock();
            }
        }

        return buildUserInfoWrapper(token);
    }

    private UserInfoWrapper getUserInfo(String subClaim, String token) throws Auth0Exception {
        UserInfoWrapper userInfoWrapper = userInfoCache.getIfPresent(subClaim);
        if (userInfoWrapper == null) {
            userInfoWrapper = buildUserInfoWrapper(token);
            userInfoCache.put(subClaim, userInfoWrapper);
        }
        return userInfoWrapper;
    }

    private UserInfoWrapper buildUserInfoWrapper(String token) throws Auth0Exception {
        Map<String, Object> userInfo = authApi.userInfo(token).execute().getValues();
        Customer customer = getOrCreateCustomer(userInfo);
        return new UserInfoWrapper(userInfo, customer);
    }

    private String getAccessToken(String userId) {
        try {
            return managementApiSupplier.get().users().get(userId, null).execute()
                    .getIdentities().stream()
                    .filter(identity -> identity.getProvider().equals(ACCESS_TOKEN))
                    .findFirst()
                    .map(Identity::getAccessToken)
                    .orElseThrow(NoAccessTokenException::new);
        } catch (Auth0Exception authException) {
            throw new ManagementApiException(authException);
        }
    }

    private Customer getOrCreateCustomer(Map<String, Object> userInfo) {
        return customerService.getOrCreate(userInfo.get(NAME_FIELD).toString(), userInfo.get(EMAIL_FIELD).toString());
    }

    private String getToken(NativeWebRequest webRequest) {
        String authHeader = webRequest.getHeader(AUTHORIZATION);
        Assert.notNull(authHeader, "auth header is required");
        return authHeader.replace(TOKEN_PREFIX, "");
    }

}
