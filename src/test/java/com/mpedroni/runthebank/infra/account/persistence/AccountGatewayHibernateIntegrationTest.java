package com.mpedroni.runthebank.infra.account.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.mpedroni.runthebank.domain.transaction.TransactionStatus;
import com.mpedroni.runthebank.infra.client.persistence.ClientJpaEntity;
import com.mpedroni.runthebank.infra.client.persistence.ClientRepository;
import com.mpedroni.runthebank.infra.client.persistence.ClientTypeJpa;
import com.mpedroni.runthebank.infra.transaction.persistence.TransactionJpaEntity;
import com.mpedroni.runthebank.infra.transaction.persistence.TransactionRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class AccountGatewayHibernateIntegrationTest {
    private AccountJpaEntity john;
    private AccountJpaEntity jane;

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TestEntityManager em;

    @Autowired
    TransactionRepository transactionRepository;

    AccountGatewayHibernate sut;

    void createAccounts() {
        var johnClient = new ClientJpaEntity(UUID.randomUUID(), "John Doe", "12345678900", "Address", "password", ClientTypeJpa.CUSTOMER);
        var janeClient = new ClientJpaEntity(UUID.randomUUID(), "Jane Doe", "12345678901", "Address", "password", ClientTypeJpa.CUSTOMER);
        clientRepository.saveAll(List.of(johnClient, janeClient));

        john = new AccountJpaEntity(UUID.randomUUID(), johnClient.getId(), 1234, 1);
        jane = new AccountJpaEntity(UUID.randomUUID(), janeClient.getId(), 1234, 2);
        accountRepository.saveAll(List.of(john, jane));
    }

    void cleanDatabase() {
        transactionRepository.deleteAll();
        clientRepository.deleteAll();
    }

    @BeforeEach
    void setup() {
        sut = new AccountGatewayHibernate(accountRepository);
        cleanDatabase();
        createAccounts();
    }

    TransactionJpaEntity aTransferFrom(AccountJpaEntity from, AccountJpaEntity to, double amount) {
        var transfer = TransactionJpaEntity.transferOf(from.getId(), to.getId(), BigDecimal.valueOf(amount));
        transfer.setStatus(TransactionStatus.COMPLETED);
        em.persist(transfer);
        return transfer;
    }


    void aCanceledTransferFrom(AccountJpaEntity payer, AccountJpaEntity payee, double amount) {
        var transfer = aTransferFrom(payer, payee, amount);
        transfer.setStatus(TransactionStatus.CANCELED);
        em.persist(transfer);
    }

    TransactionJpaEntity aDeposit(AccountJpaEntity account, double amount) {
        var deposit = TransactionJpaEntity.depositOf(account, BigDecimal.valueOf(amount), TransactionStatus.COMPLETED);
        em.persist(deposit);
        return deposit;
    }

    void aPendingDeposit(AccountJpaEntity account, double amount) {
        var deposit = aDeposit(account, amount);
        deposit.setStatus(TransactionStatus.PENDING);
        em.persist(deposit);
    }

    void aCanceledDeposit(AccountJpaEntity account, double amount) {
        var deposit = aDeposit(account, amount);
        deposit.setStatus(TransactionStatus.CANCELED);
        em.persist(deposit);
    }

    float balanceOf(AccountJpaEntity account) {
        return sut.findById(account.getId()).orElseThrow().balance().floatValue();
    }

    @Test
    void returnsZeroAsBalanceWhenAccountHasNoAssociatedTransactions() {
        assertThat(balanceOf(john)).isEqualTo(0f);
    }

    @Test
    void returnsAccountBalanceAsTheSumOfAllItsAssociatedTransactions() {
        aDeposit(jane, 200);
        aTransferFrom(jane, john, 100);
        aTransferFrom(john, jane, 50);

        assertThat(balanceOf(john)).isEqualTo(50f);
        assertThat(balanceOf(jane)).isEqualTo(150f);
    }

    @Test
    void dontConsiderPendingDepositsWhenCalculatingBalance() {
        aPendingDeposit(john, 100);

        assertThat(balanceOf(john)).isEqualTo(0f);
    }

    @Test
    void dontConsiderCanceledTransactionsWhenCalculatingBalance() {
        aCanceledDeposit(john, 100);
        aCanceledTransferFrom(jane, john, 100);

        assertThat(balanceOf(john)).isEqualTo(0f);
    }
}
