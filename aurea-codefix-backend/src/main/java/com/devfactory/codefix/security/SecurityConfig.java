package com.devfactory.codefix.security;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.PRAGMA;

import com.auth0.client.auth.AuthAPI;
import com.auth0.spring.security.api.JwtWebSecurityConfigurer;
import com.devfactory.codefix.customers.services.CustomerService;
import com.devfactory.codefix.security.token.ManagementApiSupplier;
import com.devfactory.codefix.security.web.AuthInformationResolver;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Contains auth0 jwt token security configuration.
 */
@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        JwtWebSecurityConfigurer
                .forRS256(securityProperties().getAudience(), securityProperties().getIssuer())
                .configure(http)
                .authorizeRequests()
                .antMatchers("/api").authenticated()
                .and()
                .cors();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(authResolver(null, null, null));
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(singletonList("*"));
        configuration.setAllowedMethods(asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(asList(PRAGMA, AUTHORIZATION, CACHE_CONTROL, CONTENT_TYPE));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    @ConfigurationProperties(prefix = "app.security")
    SecurityProperties securityProperties() {
        return new SecurityProperties();
    }

    @Bean
    AuthAPI authApi(SecurityProperties props) {
        return new AuthAPI(props.getDomain(), props.getClientId(), props.getClientSecret());
    }

    @Bean
    ManagementApiSupplier managementApi(SecurityProperties props, AuthAPI authApi) {
        return new ManagementApiSupplier(props, authApi);
    }

    @Bean
    AuthInformationResolver authResolver(CustomerService customerService, AuthAPI authApi, ManagementApiSupplier api) {
        return new AuthInformationResolver(customerService, authApi, api);
    }
}
