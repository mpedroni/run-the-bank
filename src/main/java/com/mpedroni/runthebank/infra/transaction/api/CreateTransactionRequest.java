package com.mpedroni.runthebank.infra.transaction.api;

import java.util.UUID;

public record CreateTransactionRequest(
    UUID payerAccountId,
    UUID payeeAccountId,
    double amount
) {

}
