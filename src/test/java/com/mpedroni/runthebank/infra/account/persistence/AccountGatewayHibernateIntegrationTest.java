package com.mpedroni.runthebank.infra.account.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.mpedroni.runthebank.domain.transaction.TransactionStatus;
import com.mpedroni.runthebank.domain.transaction.TransactionType;
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
    }

    static AccountJpaEntity anAccount(int number) {
        return new AccountJpaEntity(UUID.randomUUID(), UUID.randomUUID(), 1234, number);
    }

    static TransactionJpaEntity aTransferAsPayer(UUID accountId, double amount) {
        var payeeId = UUID.randomUUID();
        return TransactionJpaEntity.transferOf(accountId, payeeId, BigDecimal.valueOf(amount));
    }

    static TransactionJpaEntity aTransferAsPayee(UUID accountId, double amount) {
        var payerId = UUID.randomUUID();
        return TransactionJpaEntity.transferOf(payerId, accountId, BigDecimal.valueOf(amount));
    }

    static TransactionJpaEntity aCanceledTransferAsPayee(UUID accountId, double amount) {
        var transfer = aTransferAsPayee(accountId, amount);
        transfer.setStatus(TransactionStatus.CANCELED);
        return transfer;
    }

    static TransactionJpaEntity aDeposit(UUID accountId, double amount) {
        return TransactionJpaEntity.depositOf(accountId, BigDecimal.valueOf(amount), TransactionStatus.COMPLETED);
    }

    static TransactionJpaEntity aPendingDeposit(UUID accountId, double amount) {
        var deposit = aDeposit(accountId, amount);
        deposit.setStatus(TransactionStatus.PENDING);
        return deposit;
    }

    static TransactionJpaEntity aCanceledDeposit(UUID accountId, double amount) {
        var deposit = aDeposit(accountId, amount);
        deposit.setStatus(TransactionStatus.CANCELED);
        return deposit;
    }

    @Test
    void returnsZeroAsBalanceWhenAccountHasNoAssociatedTransactions() {
        var anAccount = anAccount(1);

        em.persist(anAccount);

        var balance = sut.findById(anAccount.getId()).get().balance();

        assertThat(balance).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void returnsAccountBalanceAsTheSumOfAllItsAssociatedTransactions() {
        var anAccount = anAccount(1);

        em.persist(anAccount);

        em.persist(aTransferAsPayee(anAccount.getId(), 100));
        em.persist(aTransferAsPayee(anAccount.getId(), 100));
        em.persist(aTransferAsPayer(anAccount.getId(), 50));

        var balance = sut.findById(anAccount.getId()).get().balance();

        assertThat(balance.floatValue()).isEqualTo(150f);
    }

    @Test
    void dontConsiderPendingDepositsWhenCalculatingBalance() {
        var anAccount = anAccount(1);

        em.persist(anAccount);

        em.persist(aTransferAsPayee(anAccount.getId(), 100));
        em.persist(aTransferAsPayee(anAccount.getId(), 100));
        em.persist(aPendingDeposit(anAccount.getId(), 100));

        var balance = sut.findById(anAccount.getId()).get().balance();

        assertThat(balance.floatValue()).isEqualTo(200f);
    }

    @Test
    void dontConsiderCanceledTransactionsWhenCalculatingBalance() {
        var anAccount = anAccount(1);

        em.persist(anAccount);

        em.persist(aTransferAsPayee(anAccount.getId(), 25));
        em.persist(aTransferAsPayee(anAccount.getId(), 50));

        em.persist(aCanceledDeposit(anAccount.getId(), 100));
        em.persist(aCanceledTransferAsPayee(anAccount.getId(), 100));

        var balance = sut.findById(anAccount.getId()).get().balance();

        assertThat(balance.floatValue()).isEqualTo(75f);
    }
}
