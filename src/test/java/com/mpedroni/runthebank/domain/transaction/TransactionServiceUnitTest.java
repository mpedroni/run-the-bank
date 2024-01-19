package com.mpedroni.runthebank.domain.transaction;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mpedroni.runthebank.domain.ApplicationException;
import com.mpedroni.runthebank.domain.account.Account;
import com.mpedroni.runthebank.domain.transaction.api.TransactionService;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class TransactionServiceUnitTest {
    TransactionService sut = new TransactionService();

    @Test
    void throwsIfPayerAndPayeeAreTheSame() {
        var payer = new Account(
            UUID.randomUUID(),
            UUID.randomUUID(),
            1234,
            1,
            BigDecimal.valueOf(0)
        );

        assertThatThrownBy(() -> sut.createTransaction(payer, payer, BigDecimal.valueOf(100)))
            .isInstanceOf(ApplicationException.class)
            .hasMessage("Payer and payee cannot be the same.");
    }

}
