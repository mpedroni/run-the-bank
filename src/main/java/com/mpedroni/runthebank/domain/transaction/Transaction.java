package com.mpedroni.runthebank.domain.transaction;

import com.mpedroni.runthebank.domain.account.Account;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class Transaction {

    private final UUID id;
    private final Account payer;
    private final Account payee;
    private final BigDecimal amount;
    private final TransactionType type;
    private TransactionStatus status;

    public Transaction(
        UUID id,
        Account payer,
        Account payee,
        BigDecimal amount,
        TransactionType type,
        TransactionStatus status
    ) {
        this.id = id;
        this.payer = payer;
        this.payee = payee;
        this.amount = amount;
        this.type = type;
        this.status = status;
    }

    public static Transaction create(Account payer, Account payee, BigDecimal amount,
        TransactionType type) {
        return new Transaction(
            UUID.randomUUID(),
            payer,
            payee,
            amount,
            type,
            TransactionStatus.PENDING
        );
    }

    public UUID id() {
        return id;
    }

    public Account payer() {
        return payer;
    }

    public Account payee() {
        return payee;
    }

    public BigDecimal amount() {
        return amount;
    }

    public TransactionType type() {
        return type;
    }

    public TransactionStatus status() {
        return status;
    }

    public void complete() {
        if (status != TransactionStatus.PENDING) {
            throw new IllegalStateException("Transaction is not pending.");
        }

        status = TransactionStatus.COMPLETED;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (Transaction) obj;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction[" +
            "id=" + id + ", " +
            "payer=" + payer + ", " +
            "payee=" + payee + ", " +
            "amount=" + amount + ", " +
            "type=" + type + ", " +
            "status=" + status + ']';
    }

}
