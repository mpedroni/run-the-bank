package com.mpedroni.runthebank.infra.account.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class AccountRepositoryIntegrationTests {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TestEntityManager em;

    @Test
    void findsTheLastAccountNumberFromAnAgency() {
        var anAgency = 1234;
        var anAccountNumber = 1;
        var lastAccountNumber = 2;

        em.persist(new AccountJpaEntity(UUID.randomUUID(), UUID.randomUUID(), anAgency, anAccountNumber));
        em.persist(new AccountJpaEntity(UUID.randomUUID(), UUID.randomUUID(), anAgency, lastAccountNumber));

        var foundLastAccountNumber = accountRepository.findLastAccountNumberFrom(anAgency);

        assertThat(foundLastAccountNumber).isEqualTo(lastAccountNumber);
    }

    @Test
    void returnsZeroAsLastAccountNumberWhenThereAreNoAccountsFromAnAgency() {
        var anAgency = 1234;

        var foundLastAccountNumber = accountRepository.findLastAccountNumberFrom(anAgency);

        assertThat(foundLastAccountNumber).isEqualTo(0);
    }

    @Test
    void throwsWhenCreatingAnAccountWithDuplicatedNumberAndAgency() {
        var anAgency = 1234;
        var anAccountNumber = 1;

        var anAccount = new AccountJpaEntity(UUID.randomUUID(), UUID.randomUUID(), anAgency, anAccountNumber);
        var anotherAccount = new AccountJpaEntity(UUID.randomUUID(), UUID.randomUUID(), anAgency, anAccountNumber);

        accountRepository.saveAndFlush(anAccount);

        assertThatThrownBy(() -> accountRepository.saveAndFlush(anotherAccount))
            .isInstanceOf(DataIntegrityViolationException.class)
            .hasMessageContaining("ACCOUNTS_AGENCY_NUMBER_UNIQUE");
    }

}