package com.mpedroni.runthebank.infra.transaction.api;

import com.mpedroni.runthebank.domain.transaction.TransactionType;
import java.util.UUID;

public record CreateTransactionRequest(
    UUID payerAccountId,
    UUID payeeAccountId,
    double amount,
    TransactionType type
) {

}
