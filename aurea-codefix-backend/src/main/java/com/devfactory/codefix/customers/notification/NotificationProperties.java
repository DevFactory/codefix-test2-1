package com.devfactory.codefix.customers.notification;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.notification")
public class NotificationProperties {

    private String frontendUrl;
}
