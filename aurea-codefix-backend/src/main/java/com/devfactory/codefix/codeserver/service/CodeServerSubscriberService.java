package com.devfactory.codefix.codeserver.service;

import com.devfactory.codeserver.client.CodeServerClient;
import com.devfactory.codeserver.client.NotificationApi;
import com.devfactory.codeserver.model.CommitProcessed;
import com.devfactory.codeserver.model.Notification;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.InitializingBean;

@AllArgsConstructor
@Slf4j
public class CodeServerSubscriberService implements InitializingBean {

    private final CodeServerMessageListener messageListener;
    private final CodeServerClient codeServerClient;

    private static final String CONSUMER_NAME = "codefix-subscriber-service";

    @Override
    public void afterPropertiesSet() throws Exception {
        subscribe(messageListener);
    }

    private void subscribe(MessageListener listener) throws URISyntaxException, JMSException {
        log.info("Subscribing to 'commit processed' notification queue");
        NotificationApi notificationsApi = codeServerClient.notifications();

        List<Notification> list = notificationsApi.list();
        Optional<Notification> commitProcessedNotificationQueue = list.stream()
                .filter(notification -> CommitProcessed.class.getName().equalsIgnoreCase(notification.getType()))
                .findFirst();

        if (commitProcessedNotificationQueue.isPresent()) {
            subscribe(commitProcessedNotificationQueue.get(), listener);
            return;
        }

        log.error("CodeServer notification queue not found for {} notification type", CommitProcessed.class.getName());
    }

    private void subscribe(Notification notification, MessageListener listener)
            throws URISyntaxException, JMSException {
        URI topicUri = new URI(notification.getBrokerUrl());
        ConnectionFactory factory = new ActiveMQConnectionFactory(topicUri);
        Connection connection = factory.createConnection();
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        connection.start();

        String queueNameFormat = notification.getConsumerQueueNameFormat();
        String queueName = queueNameFormat.replace("%", CONSUMER_NAME);

        Destination destination = session.createQueue(queueName);
        MessageConsumer consumer = session.createConsumer(destination);
        consumer.setMessageListener(listener);

        log.info("Subscribed to '{}' queue successfully.", queueName);
    }
}
