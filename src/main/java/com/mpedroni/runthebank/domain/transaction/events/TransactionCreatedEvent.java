package com.mpedroni.runthebank.domain.transaction.events;

import java.util.UUID;

public record TransactionCreatedEvent(
    UUID id
) {

}
