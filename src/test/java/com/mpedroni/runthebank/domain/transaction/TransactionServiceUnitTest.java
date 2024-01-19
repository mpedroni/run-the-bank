package com.mpedroni.runthebank.domain.transaction;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mpedroni.runthebank.domain.ApplicationException;
import com.mpedroni.runthebank.domain.account.Account;
import com.mpedroni.runthebank.domain.account.AccountStatus;
import com.mpedroni.runthebank.domain.transaction.api.TransactionService;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class TransactionServiceUnitTest {
    TransactionService sut = new TransactionService();

    @Test
    void throwsIfPayerAndPayeeAreTheSame() {
        var payer = Account.create(
            UUID.randomUUID(),
            1234,
            1
        );

        assertThatThrownBy(() -> sut.createTransaction(payer, payer, BigDecimal.valueOf(100)))
            .isInstanceOf(ApplicationException.class)
            .hasMessage("Payer and payee cannot be the same.");
    }

    @Test
    void throwsIfPayerIsNotActive() {
        var payer = new Account(
            UUID.randomUUID(),
            UUID.randomUUID(),
            1234,
            1,
            BigDecimal.valueOf(100),
            AccountStatus.INACTIVE
        );

        var payee = Account.create(
            UUID.randomUUID(),
            1234,
            2
        );

        assertThatThrownBy(() -> sut.createTransaction(payer, payee, BigDecimal.valueOf(100)))
            .isInstanceOf(ApplicationException.class)
            .hasMessage("Payer account is not active.");
    }

    @Test
    void throwsIfPayeeIsNotActive() {
        var payer = Account.create(
            UUID.randomUUID(),
            1234,
            1
        );

        var payee = new Account(
            UUID.randomUUID(),
            UUID.randomUUID(),
            1234,
            2,
            BigDecimal.valueOf(100),
            AccountStatus.INACTIVE
        );

        assertThatThrownBy(() -> sut.createTransaction(payer, payee, BigDecimal.valueOf(100)))
            .isInstanceOf(ApplicationException.class)
            .hasMessage("Payee account is not active.");
    }

}
