package com.mpedroni.runthebank.domain.transaction;

import java.math.BigDecimal;
import java.util.UUID;

public record Transaction(
    UUID id,
    UUID payerId,
    UUID payeeId,
    BigDecimal amount
) {

}
