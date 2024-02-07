package com.mpedroni.runthebank.domain.transaction;

import java.util.Optional;
import java.util.UUID;

public interface TransactionGateway {
    void create(Transaction transaction);
    Optional<Transaction> findById(UUID id);
    void save(Transaction transaction);
}
