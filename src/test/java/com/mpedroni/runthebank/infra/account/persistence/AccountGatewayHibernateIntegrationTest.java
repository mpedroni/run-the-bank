package com.mpedroni.runthebank.infra.account.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.mpedroni.runthebank.infra.transaction.persistence.TransactionJpaEntity;
import com.mpedroni.runthebank.infra.transaction.persistence.TransactionRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

@DataJpaTest
public class AccountGatewayHibernateIntegrationTest {
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

    static TransactionJpaEntity aTransactionAsPayer(UUID accountId, double amount) {
        var payeeId = UUID.randomUUID();
        return TransactionJpaEntity.transferOf(accountId, payeeId, BigDecimal.valueOf(amount));
    }

    static TransactionJpaEntity aTransactionAsPayee(UUID accountId, double amount) {
        var payerId = UUID.randomUUID();
        return TransactionJpaEntity.transferOf(payerId, accountId, BigDecimal.valueOf(amount));
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

        em.persist(aTransactionAsPayee(anAccount.getId(), 100));
        em.persist(aTransactionAsPayee(anAccount.getId(), 100));
        em.persist(aTransactionAsPayer(anAccount.getId(), 50));

        var balance = sut.findById(anAccount.getId()).get().balance();

        assertThat(balance.floatValue()).isEqualTo(150f);
    }
}
