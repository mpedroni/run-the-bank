package com.mpedroni.runthebank.domain.transaction.api;

import com.mpedroni.runthebank.domain.ApplicationException;
import com.mpedroni.runthebank.domain.account.Account;
import com.mpedroni.runthebank.domain.transaction.Transaction;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

    public Transaction createTransaction(Account payer, Account payee, BigDecimal amount) {
        if (payer.id().equals(payee.id())) {
            throw new ApplicationException("Payer and payee cannot be the same.");
        }

        var transaction = new Transaction(UUID.randomUUID(), payer.id(), payee.id(), amount);

        return transaction;
    }
}
