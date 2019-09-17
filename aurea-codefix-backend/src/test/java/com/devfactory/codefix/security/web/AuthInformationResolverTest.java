package com.devfactory.codefix.security.web;

import static com.google.common.collect.ImmutableMap.of;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.auth0.client.auth.AuthAPI;
import com.auth0.client.mgmt.ManagementAPI;
import com.auth0.client.mgmt.UsersEntity;
import com.auth0.exception.Auth0Exception;
import com.auth0.json.auth.UserInfo;
import com.auth0.json.mgmt.users.Identity;
import com.auth0.json.mgmt.users.User;
import com.auth0.net.Request;
import com.devfactory.codefix.customers.persistence.Customer;
import com.devfactory.codefix.customers.services.CustomerService;
import com.devfactory.codefix.security.exception.ManagementApiException;
import com.devfactory.codefix.security.token.ManagementApiSupplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

@ExtendWith(MockitoExtension.class)
class AuthInformationResolverTest {

    private static final String USER_ID = "jhon_123";
    private static final String EMPTY_JSON_BASE64 = "e30=";
    private static final String JWT_PAYLOAD_WITH_SUB = "eyJzdWIiOiJnaXRodWJ8MTIzNDU2NzgifQ==";
    private static final String TOKEN_WITH_SUB = EMPTY_JSON_BASE64 + "." + JWT_PAYLOAD_WITH_SUB + "."
            + EMPTY_JSON_BASE64;
    private static final String TOKEN_WITHOUT_SUB = EMPTY_JSON_BASE64 + "." + EMPTY_JSON_BASE64 + "."
            + EMPTY_JSON_BASE64;
    private static final String ACCESS_TOKEN = "xyz987";
    private static final String USER_NAME = "jhon doe";
    private static final String USER_EMAIL = "jhon_deo@mail.com";

    @Mock
    private AuthAPI authApi;

    @Mock
    private CustomerService customerService;

    @Mock
    private MethodParameter methodParameter;

    @Mock
    private MethodParameter parameter;

    @Mock
    private ModelAndViewContainer mavContainer;

    @Mock
    private NativeWebRequest webRequest;

    @Mock
    private WebDataBinderFactory binderFactory;

    @Mock
    private Request<UserInfo> request;

    @Mock
    private ManagementApiSupplier apiSupplier;

    @Mock
    private UserInfo userInfo;

    @Mock
    private ManagementAPI managementApi;

    @Mock
    private UsersEntity usersEntity;

    @Mock
    private User user;

    @Mock
    private Request<User> userRequest;

    @Mock
    private Identity identity;

    private AuthInformationResolver testInstance;

    @BeforeEach
    void beforeEach() {
        testInstance = new AuthInformationResolver(customerService, authApi, apiSupplier);
    }

    @Test
    void supportsParameterWhenSupported() {
        doReturn(AuthInformation.class).when(methodParameter).getParameterType();

        assertThat(testInstance.supportsParameter(methodParameter)).isTrue();
    }

    @Test
    void supportsParameterWhenNotSupported() {
        doReturn(String.class).when(methodParameter).getParameterType();

        assertThat(testInstance.supportsParameter(methodParameter)).isFalse();
    }

    @Nested
    class ResolveArgument {

        private final Customer customer = new Customer();

        @BeforeEach
        void beforeEach() throws Exception {
            given(customerService.getOrCreate(USER_NAME, USER_EMAIL)).willReturn(customer);
            given(webRequest.getHeader(AUTHORIZATION)).willReturn("Bearer " + TOKEN_WITH_SUB);
            given(authApi.userInfo(anyString())).willReturn(request);
            given(request.execute()).willReturn(userInfo);
            given(userInfo.getValues()).willReturn(of("name", USER_NAME, "email", USER_EMAIL, "sub", USER_ID));

            given(apiSupplier.get()).willReturn(managementApi);
            given(managementApi.users()).willReturn(usersEntity);
            given(usersEntity.get(USER_ID, null)).willReturn(userRequest);
            given(userRequest.execute()).willReturn(user);
        }

        @Test
        void resolveArgument() throws Exception {
            mockIdentity();

            AuthInformation argument = testInstance
                    .resolveArgument(parameter, mavContainer, webRequest, binderFactory);

            assertThat(argument).isNotNull();
            assertThat(argument.getCustomer()).isEqualTo(customer);
            assertThat(argument.getToken()).isEqualTo(TOKEN_WITH_SUB);
            assertThat(argument.getAccessToken()).isEqualTo(ACCESS_TOKEN);
        }

        @Test
        void resolveArgumentWhenException() throws Exception {
            Auth0Exception exception = mock(Auth0Exception.class);
            given(userRequest.execute()).willThrow(exception);

            AuthInformation argument = testInstance
                    .resolveArgument(parameter, mavContainer, webRequest, binderFactory);

            assertThat(argument).isNotNull();
            assertThat(argument.getCustomer()).isEqualTo(customer);
            assertThat(argument.getToken()).isEqualTo(TOKEN_WITH_SUB);

            ManagementApiException apiException = assertThrows(ManagementApiException.class, argument::getAccessToken);
            assertThat(apiException.getCause()).isEqualTo(exception);
        }

        @Test
        void userInfoIsCached() throws Exception {
            mockIdentity();

            AuthInformation authInfo1 = testInstance
                    .resolveArgument(parameter, mavContainer, webRequest, binderFactory);

            AuthInformation authInfo2 = testInstance
                    .resolveArgument(parameter, mavContainer, webRequest, binderFactory);

            assertThat(authInfo1).isNotNull();
            assertThat(authInfo1.getCustomer()).isEqualTo(customer);
            assertThat(authInfo1.getToken()).isEqualTo(TOKEN_WITH_SUB);
            assertThat(authInfo1.getAccessToken()).isEqualTo(ACCESS_TOKEN);

            assertThat(authInfo2).isNotNull();
            assertThat(authInfo2.getCustomer()).isEqualTo(customer);
            assertThat(authInfo2.getToken()).isEqualTo(TOKEN_WITH_SUB);
            assertThat(authInfo2.getAccessToken()).isEqualTo(ACCESS_TOKEN);

            verify(authApi).userInfo(anyString());
        }

        @Test
        void userInfoIsNotCached() throws Exception {
            given(webRequest.getHeader(AUTHORIZATION)).willReturn("Bearer " + TOKEN_WITHOUT_SUB);
            mockIdentity();

            AuthInformation authInfo1 = testInstance
                    .resolveArgument(parameter, mavContainer, webRequest, binderFactory);

            AuthInformation authInfo2 = testInstance
                    .resolveArgument(parameter, mavContainer, webRequest, binderFactory);

            assertThat(authInfo1).isNotNull();
            assertThat(authInfo1.getCustomer()).isEqualTo(customer);
            assertThat(authInfo1.getToken()).isEqualTo(TOKEN_WITHOUT_SUB);
            assertThat(authInfo1.getAccessToken()).isEqualTo(ACCESS_TOKEN);

            assertThat(authInfo2).isNotNull();
            assertThat(authInfo2.getCustomer()).isEqualTo(customer);
            assertThat(authInfo2.getToken()).isEqualTo(TOKEN_WITHOUT_SUB);
            assertThat(authInfo2.getAccessToken()).isEqualTo(ACCESS_TOKEN);

            verify(authApi, times(2)).userInfo(anyString());
        }

        private void mockIdentity() throws Auth0Exception {
            given(userRequest.execute()).willReturn(user);
            given(user.getIdentities()).willReturn(singletonList(identity));
            given(identity.getAccessToken()).willReturn(ACCESS_TOKEN);
            given(identity.getProvider()).willReturn("github");
        }
    }
}
