package com.devfactory.codefix.aws;

import static org.assertj.core.api.Assertions.assertThat;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentialsProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AwsConfigTest {

    @Mock
    private AWSCredentialsProvider credentialsProvider;

    @Mock
    private SQSConnectionFactory sqsConnectionFactory;


    @InjectMocks
    private AwsConfig testInstance;

    @Test
    void amazonSimpleEmailService() {
        assertThat(testInstance.amazonSimpleEmailService(credentialsProvider)).isNotNull();
    }

    @Test
    void connectionFactory() {
        assertThat(testInstance.connectionFactory(credentialsProvider)).isNotNull();
    }

    @Test
    void defaultJmsTemplate() {
        assertThat(testInstance.defaultJmsTemplate(sqsConnectionFactory)).isNotNull();
    }

    @Test
    void jmsListenerContainerFactory() {
        assertThat(testInstance.jmsListenerContainerFactory(sqsConnectionFactory)).isNotNull();
    }

    @Test
    void credentialsProvider() {
        assertThat(testInstance.credentialsProvider()).isNotNull();
    }

    @Test
    void sesProperties() {
        assertThat(testInstance.sesProperties()).isNotNull();
    }
}
