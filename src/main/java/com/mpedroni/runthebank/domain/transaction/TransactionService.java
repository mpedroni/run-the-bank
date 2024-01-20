package com.mpedroni.runthebank.domain.transaction;

import com.mpedroni.runthebank.domain.ApplicationException;
import com.mpedroni.runthebank.domain.account.Account;
import com.mpedroni.runthebank.domain.transaction.exceptions.NotEnoughBalanceException;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final TransactionGateway transactionGateway;

    public TransactionService(TransactionGateway transactionGateway) {
        this.transactionGateway = transactionGateway;
    }

    public Transaction createTransaction(Account payer, Account payee, BigDecimal amount) {
        if (payer.id().equals(payee.id())) {
            throw new ApplicationException("Payer and payee cannot be the same.");
        }

        if (!payer.isActive()) {
            throw new ApplicationException("Payer account is not active.");
        }

        if (!payee.isActive()) {
            throw new ApplicationException("Payee account is not active.");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ApplicationException("Amount must be greater than zero.");
        }

        if (payer.balance().compareTo(amount) < 0) {
            throw new NotEnoughBalanceException("Payer account does not have enough balance.");
        }

        var transaction = new Transaction(UUID.randomUUID(), payer, payee, amount);
        transactionGateway.create(transaction);

        return transaction;
    }
}
