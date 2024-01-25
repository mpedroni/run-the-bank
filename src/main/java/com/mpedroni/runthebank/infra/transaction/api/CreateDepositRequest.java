package com.mpedroni.runthebank.infra.transaction.api;

import java.util.UUID;

public record CreateDepositRequest(
    UUID accountId,
    double amount
) {

}
