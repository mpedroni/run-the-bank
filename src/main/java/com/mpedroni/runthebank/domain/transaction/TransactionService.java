package com.mpedroni.runthebank.domain.transaction;

import com.mpedroni.runthebank.domain.ValidationError;
import com.mpedroni.runthebank.domain.account.Account;
import com.mpedroni.runthebank.domain.transaction.exceptions.InactiveAccountException;
import com.mpedroni.runthebank.domain.transaction.exceptions.NotEnoughBalanceException;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    private final TransactionGateway transactionGateway;

    public TransactionService(TransactionGateway transactionGateway) {
        this.transactionGateway = transactionGateway;
    }

    private Transaction createTransaction(Account payer, Account payee, BigDecimal amount,
        TransactionType type) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationError("Amount must be greater than zero.");
        }

        var transaction = Transaction.create(payer, payee, amount, type);
        transactionGateway.create(transaction);

        return transaction;
    }

    public Transaction deposit(Account account, BigDecimal amount) {
        if (!account.isActive()) {
            throw new ValidationError("Account is not active.");
        }

        return createTransaction(null, account, amount, TransactionType.DEPOSIT);
    }

    public Transaction transfer(Account payer, Account payee, BigDecimal amount) {
        if (!payer.isActive()) {
            throw new InactiveAccountException("Payer account is not active.");
        }

        if (!payee.isActive()) {
            throw new InactiveAccountException("Payee account is not active.");
        }

        if (payer.id().equals(payee.id())) {
            throw new ValidationError("Payer and payee cannot be the same.");
        }


        if (payer.balance().compareTo(amount) < 0) {
            throw new NotEnoughBalanceException("Payer account does not have enough balance.");
        }

        return createTransaction(payer, payee, amount, TransactionType.TRANSFER);
    }
}
