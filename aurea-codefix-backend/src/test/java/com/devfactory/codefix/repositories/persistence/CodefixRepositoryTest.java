package com.devfactory.codefix.repositories.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.devfactory.codefix.customers.persistence.Customer;
import org.junit.jupiter.api.Test;

class CodefixRepositoryTest {

    private Customer repoCustomer = new Customer().setId(10L);
    private Customer anotherCustomer = new Customer().setId(11L);

    private CodefixRepository testInstance = new CodefixRepository().setCustomer(repoCustomer);

    @Test
    void belongsToWhenDoNotBelong() {
        assertThat(testInstance.belongsTo(anotherCustomer)).isFalse();
        assertThat(testInstance.notBelongsTo(anotherCustomer)).isTrue();
    }

    @Test
    void belongsToWhenBelong() {
        assertThat(testInstance.belongsTo(repoCustomer)).isTrue();
        assertThat(testInstance.notBelongsTo(repoCustomer)).isFalse();
    }
}
