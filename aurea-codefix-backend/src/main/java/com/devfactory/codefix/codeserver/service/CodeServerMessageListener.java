package com.devfactory.codefix.codeserver.service;

import com.devfactory.base.json.JacksonConverter;
import com.devfactory.codeserver.model.CommitProcessed;
import com.devfactory.codeserver.model.NotificationWrapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class CodeServerMessageListener implements MessageListener {

    private final CommitProcessedNotificationHandler commitProcessedNotificationHandler;

    @Override
    public void onMessage(Message msg) {
        try {
            NotificationWrapper wrapper = getNotificationWrapper(msg);

            String type = wrapper.getType();

            log.info("Received {} notification - {}", type, wrapper.getBody());

            if (type.contains(CommitProcessed.class.getSimpleName())) {
                CommitProcessed notification = getObject(wrapper.getBody(), CommitProcessed.class);
                commitProcessedNotificationHandler.handle(notification);
            }
        } catch (JMSException | IOException ex) {
            log.error("Error while parsing JMS message", ex);
        }
    }

    private NotificationWrapper getNotificationWrapper(Message msg) throws JMSException, IOException {
        if (msg instanceof TextMessage) {
            return getObject(((TextMessage) msg).getText(), NotificationWrapper.class);
        }

        return getObject((BytesMessage) msg, NotificationWrapper.class);
    }

    @SneakyThrows
    private <T> T getObject(BytesMessage message, Class<T> clazz) {
        int textLength = Math.toIntExact(message.getBodyLength());
        byte[] textBytes = new byte[textLength];
        message.readBytes(textBytes, textLength);
        String data = new String(textBytes, StandardCharsets.UTF_8);
        return getObject(data, clazz);
    }

    @SneakyThrows
    private <T> T getObject(String data, Class<T> clazz) {
        return JacksonConverter.fromJson(data, clazz);
    }
}
