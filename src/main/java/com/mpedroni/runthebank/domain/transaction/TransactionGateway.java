package com.mpedroni.runthebank.domain.transaction;

import com.mpedroni.runthebank.domain.transaction.Transaction;

public interface TransactionGateway {
    void create(Transaction transaction);
}
