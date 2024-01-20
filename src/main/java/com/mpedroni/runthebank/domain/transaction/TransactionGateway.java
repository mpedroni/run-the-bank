package com.mpedroni.runthebank.domain.transaction;

public interface TransactionGateway {
    void create(Transaction transaction);
}
