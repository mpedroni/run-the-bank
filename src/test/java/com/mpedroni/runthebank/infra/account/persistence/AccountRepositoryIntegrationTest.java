package com.mpedroni.runthebank.infra.account.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mpedroni.runthebank.infra.client.persistence.ClientJpaEntity;
import com.mpedroni.runthebank.infra.client.persistence.ClientRepository;
import com.mpedroni.runthebank.infra.client.persistence.ClientTypeJpa;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
class AccountRepositoryIntegrationTest {
    private static ClientJpaEntity john;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TestEntityManager em;

    @BeforeAll
    static void setUp(@Autowired ClientRepository clientRepository) {
        john = new ClientJpaEntity(UUID.randomUUID(), "John Doe", "12345678900", "Address", "password", ClientTypeJpa.CUSTOMER);
        clientRepository.saveAndFlush(john);
    }

    static AccountJpaEntity anAccount(int number) {
        return new AccountJpaEntity(UUID.randomUUID(), john.getId(), 1234, number);
    }

    static AccountJpaEntity anAccount(int number, int agency) {
        return new AccountJpaEntity(UUID.randomUUID(), john.getId(), agency, number);
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
        assertThat(lastAccountNumberForAgencyWithoutAccounts).isZero();
    }


    @Test
    void returnsZeroAsLastAccountNumberWhenThereAreNoAccountsFromAnAgency() {
        var anAgency = 1234;

        var foundLastAccountNumber = accountRepository.findLastAccountNumberFrom(anAgency);

        assertThat(foundLastAccountNumber).isZero();
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