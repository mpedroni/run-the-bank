package com.mpedroni.runthebank.domain.transaction;

import com.mpedroni.runthebank.domain.account.Account;
import java.math.BigDecimal;
import java.util.UUID;

public record Transaction(
    UUID id,
    Account payer,
    Account payee,
    BigDecimal amount
) {

}
