package com.mpedroni.runthebank.domain.transaction.api;

import com.mpedroni.runthebank.domain.transaction.Transaction;

public interface TransactionGateway {
    void create(Transaction transaction);
}
