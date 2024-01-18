package com.mpedroni.runthebank.domain.account;

import java.math.BigDecimal;
import java.util.UUID;

public record Account(
    UUID id,
    UUID clientId,
    int agency,
    int number,
    BigDecimal balance
) {

}