package com.mpedroni.runthebank.infra.account.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mpedroni.runthebank.infra.transaction.persistence.TransactionJpaEntity;
import java.math.BigDecimal;
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
    void considersOnlyAccountsFromTheGivenAgencyWhenFindingTheLastAccountNumber() {
        var agencyWithAccounts = 1111;
        var anAccountNumber = 1;
        var agencyWithoutAccounts = 2222;

        em.persist(new AccountJpaEntity(UUID.randomUUID(), UUID.randomUUID(), agencyWithAccounts, anAccountNumber));

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
        var anAgency = 1234;
        var anAccountNumber = 1;

        var anAccount = new AccountJpaEntity(UUID.randomUUID(), UUID.randomUUID(), anAgency, anAccountNumber);
        var anotherAccount = new AccountJpaEntity(UUID.randomUUID(), UUID.randomUUID(), anAgency, anAccountNumber);

        accountRepository.saveAndFlush(anAccount);

        assertThatThrownBy(() -> accountRepository.saveAndFlush(anotherAccount))
            .isInstanceOf(DataIntegrityViolationException.class)
            .hasMessageContaining("ACCOUNTS_AGENCY_NUMBER_UNIQUE");
    }

    @Test
    void hasZeroAsBalanceWhenHasNoAssociatedTransactions() {
        var anAccount = new AccountJpaEntity(UUID.randomUUID(), UUID.randomUUID(), 1234, 1);

        em.persist(anAccount);

        var balance = accountRepository.getBalanceOf(anAccount.getId());

        assertThat(balance).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void hasItsBalanceAsTheSumOfAllItsAssociatedTransactions() {
        var anAccount = new AccountJpaEntity(UUID.randomUUID(), UUID.randomUUID(), 1234, 1);

        em.persist(anAccount);

        em.persist(new TransactionJpaEntity(UUID.randomUUID(), anAccount.getId(), UUID.randomUUID(), BigDecimal.valueOf(100)));
        em.persist(new TransactionJpaEntity(UUID.randomUUID(), anAccount.getId(), UUID.randomUUID(), BigDecimal.valueOf(-50)));
        em.persist(new TransactionJpaEntity(UUID.randomUUID(), UUID.randomUUID(), anAccount.getId(), BigDecimal.valueOf(100)));

        var balance = accountRepository.getBalanceOf(anAccount.getId());

        assertThat(balance.compareTo(BigDecimal.valueOf(150)) == 0).isTrue();
    }
}