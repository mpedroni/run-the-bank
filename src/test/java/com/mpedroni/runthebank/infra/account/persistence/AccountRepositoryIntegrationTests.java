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

    static AccountJpaEntity anAccount(int number) {
        return new AccountJpaEntity(UUID.randomUUID(), UUID.randomUUID(), 1234, number);
    }

    static AccountJpaEntity anAccount(int number, int agency) {
        return new AccountJpaEntity(UUID.randomUUID(), UUID.randomUUID(), agency, number);
    }

    @Test
    void findsTheLastAccountNumberFromAnAgency() {
        var anAgency = 1234;
        var anAccountNumber = 1;
        var lastAccountNumber = 2;

        em.persist(anAccount(anAccountNumber, anAgency));
        em.persist(anAccount(lastAccountNumber, anAgency));

        var foundLastAccountNumber = accountRepository.findLastAccountNumberFrom(anAgency);

        assertThat(foundLastAccountNumber).isEqualTo(lastAccountNumber);
    }

    @Test
    void considersOnlyAccountsFromTheGivenAgencyWhenFindingTheLastAccountNumber() {
        var agencyWithAccounts = 1111;
        var anAccountNumber = 1;
        var agencyWithoutAccounts = 2222;

        em.persist(anAccount(anAccountNumber, agencyWithAccounts));

        var lastAccountNumberAgencyWithAccounts = accountRepository.findLastAccountNumberFrom(agencyWithAccounts);
        var lastAccountNumberForAgencyWithoutAccounts = accountRepository.findLastAccountNumberFrom(agencyWithoutAccounts);

        assertThat(lastAccountNumberAgencyWithAccounts).isEqualTo(anAccountNumber);
        assertThat(lastAccountNumberForAgencyWithoutAccounts).isEqualTo(0);
    }


    @Test
    void returnsZeroAsLastAccountNumberWhenThereAreNoAccountsFromAnAgency() {
        var anAgency = 1234;

        var foundLastAccountNumber = accountRepository.findLastAccountNumberFrom(anAgency);

        assertThat(foundLastAccountNumber).isEqualTo(0);
    }

    @Test
    void throwsWhenCreatingAnAccountWithDuplicatedNumberAndAgency() {
        var anAccountNumber = 1;

        var anAccount = anAccount(anAccountNumber);
        var duplicatedAccount = anAccount(anAccountNumber);

        accountRepository.saveAndFlush(anAccount);

        assertThatThrownBy(() -> accountRepository.saveAndFlush(duplicatedAccount))
            .isInstanceOf(DataIntegrityViolationException.class)
            .hasMessageContaining("ACCOUNTS_AGENCY_NUMBER_UNIQUE");
    }
}