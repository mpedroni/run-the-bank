package com.mpedroni.runthebank.infra.account.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.mpedroni.runthebank.domain.transaction.TransactionStatus;
import com.mpedroni.runthebank.infra.transaction.persistence.TransactionJpaEntity;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
class AccountGatewayHibernateIntegrationTest {
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TestEntityManager em;

    AccountGatewayHibernate sut;

    @BeforeEach
    void setup() {
        sut = new AccountGatewayHibernate(accountRepository);
        accountRepository.deleteAll();
    }

    AccountJpaEntity anAccount(int number) {
        var account = new AccountJpaEntity(UUID.randomUUID(), UUID.randomUUID(), 1234, number);
        em.persist(account);
        return account;
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
        var anAccount = anAccount(1);

        assertThat(balanceOf(anAccount)).isEqualTo(0f);
    }

    @Test
    void returnsAccountBalanceAsTheSumOfAllItsAssociatedTransactions() {
        var john = anAccount(1);
        var jane = anAccount(2);

        aDeposit(jane, 200);
        aTransferFrom(jane, john, 100);
        aTransferFrom(john, jane, 50);

        assertThat(balanceOf(john)).isEqualTo(50f);
        assertThat(balanceOf(jane)).isEqualTo(150f);
    }

    @Test
    void dontConsiderPendingDepositsWhenCalculatingBalance() {
        var anAccount = anAccount(1);

        aPendingDeposit(anAccount, 100);

        assertThat(balanceOf(anAccount)).isEqualTo(0f);
    }

    @Test
    void dontConsiderCanceledTransactionsWhenCalculatingBalance() {
        var john = anAccount(1);
        var jane = anAccount(2);

        aCanceledDeposit(john, 100);
        aCanceledTransferFrom(jane, john, 100);

        assertThat(balanceOf(john)).isEqualTo(0f);
    }
}
