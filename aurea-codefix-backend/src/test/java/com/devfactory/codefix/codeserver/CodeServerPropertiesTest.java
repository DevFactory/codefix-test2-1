package com.devfactory.codefix.codeserver;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CodeServerPropertiesTest {

    @Autowired
    private CodeServerProperties codeServerProperties;

    @Test
    void codeServerProperties() {
        assertThat(codeServerProperties.getCodeServerUrl()).isEqualTo("http://codeserver.dummy.com");
    }
}
