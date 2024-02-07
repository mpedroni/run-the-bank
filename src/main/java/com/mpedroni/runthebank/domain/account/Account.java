package com.mpedroni.runthebank.domain.account;

import java.math.BigDecimal;
import java.util.UUID;

public record Account(
    UUID id,
    UUID clientId,
    int agency,
    int number,
    BigDecimal balance,
    AccountStatus status
) {
    public Account(
        UUID id,
        UUID clientId,
        int agency,
        int number,
        BigDecimal balance
    ) {
        this(id, clientId, agency, number, balance, AccountStatus.ACTIVE);
    }

    public static Account create(
        UUID clientId,
        int agency,
        int number
    ) {
        return new Account(UUID.randomUUID(), clientId, agency, number, BigDecimal.ZERO, AccountStatus.ACTIVE);
    }

    public static Account restore(
        UUID id,
        UUID clientId,
        int agency,
        int number,
        BigDecimal balance,
        AccountStatus status
    ) {
        return new Account(id, clientId, agency, number, balance, status);
    }

    public boolean isActive() {
        return status == AccountStatus.ACTIVE;
    }
}
