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

    BigDecimal anAmount = BigDecimal.valueOf(100);

    static Account anActiveAccount(int number) {
        return Account.create(
            UUID.randomUUID(),
            1234,
            number
        );
    }

    static Account anInactiveAccount(int number) {
        return new Account(
            UUID.randomUUID(),
            UUID.randomUUID(),
            1234,
            number,
            BigDecimal.ZERO,
            AccountStatus.INACTIVE
        );
    }

    @Test
    void throwsIfPayerAndPayeeAreTheSame() {
        var payer = anActiveAccount(1);

        assertThatThrownBy(() -> sut.createTransaction(payer, payer, anAmount))
            .isInstanceOf(ApplicationException.class)
            .hasMessage("Payer and payee cannot be the same.");
    }

    @Test
    void throwsIfPayerIsNotActive() {
        var payer = anInactiveAccount(1);
        var payee = anActiveAccount(2);

        assertThatThrownBy(() -> sut.createTransaction(payer, payee, anAmount))
            .isInstanceOf(ApplicationException.class)
            .hasMessage("Payer account is not active.");
    }

    @Test
    void throwsIfPayeeIsNotActive() {
        var payer = anActiveAccount(1);
        var payee = anInactiveAccount(2);

        assertThatThrownBy(() -> sut.createTransaction(payer, payee, anAmount))
            .isInstanceOf(ApplicationException.class)
            .hasMessage("Payee account is not active.");
    }

    @Test
    void throwsIfAmountIsZeroOrLess() {
        var payer = anActiveAccount(1);
        var payee = anActiveAccount(2);

        assertThatThrownBy(() -> sut.createTransaction(payer, payee, BigDecimal.ZERO))
            .isInstanceOf(ApplicationException.class)
            .hasMessage("Amount must be greater than zero.");
    }
}
