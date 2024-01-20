package com.mpedroni.runthebank.domain.transaction;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import com.mpedroni.runthebank.domain.ApplicationException;
import com.mpedroni.runthebank.domain.account.Account;
import com.mpedroni.runthebank.domain.account.AccountStatus;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class TransactionServiceUnitTest {
    TransactionGateway transactionGateway = mock(TransactionGateway.class);

    TransactionService sut = new TransactionService(transactionGateway);

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

    @Test
    void throwsIfPayerDoesNotHaveEnoughBalance() {
        var payer = anActiveAccount(1);
        var payee = anActiveAccount(2);

        assertThatThrownBy(() -> sut.createTransaction(payer, payee, BigDecimal.valueOf(1)))
            .isInstanceOf(ApplicationException.class)
            .hasMessage("Payer account does not have enough balance.");
    }
}
