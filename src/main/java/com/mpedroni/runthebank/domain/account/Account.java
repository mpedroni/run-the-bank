package com.mpedroni.runthebank.domain.account;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public final class Account {

    private final UUID id;
    private final UUID clientId;
    private final int agency;
    private final int number;
    private final BigDecimal balance;
    private AccountStatus status;

    public Account(
        UUID id,
        UUID clientId,
        int agency,
        int number,
        BigDecimal balance,
        AccountStatus status
    ) {
        this.id = id;
        this.clientId = clientId;
        this.agency = agency;
        this.number = number;
        this.balance = balance;
        this.status = status;
    }

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
        return new Account(UUID.randomUUID(), clientId, agency, number, BigDecimal.ZERO,
            AccountStatus.ACTIVE);
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

    public UUID id() {
        return id;
    }

    public UUID clientId() {
        return clientId;
    }

    public int agency() {
        return agency;
    }

    public int number() {
        return number;
    }

    public BigDecimal balance() {
        return balance;
    }

    public AccountStatus status() {
        return status;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (Account) obj;
        return Objects.equals(this.id, that.id) &&
            Objects.equals(this.clientId, that.clientId) &&
            this.agency == that.agency &&
            this.number == that.number &&
            Objects.equals(this.balance, that.balance) &&
            Objects.equals(this.status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clientId, agency, number, balance, status);
    }

    @Override
    public String toString() {
        return "Account[" +
            "id=" + id + ", " +
            "clientId=" + clientId + ", " +
            "agency=" + agency + ", " +
            "number=" + number + ", " +
            "balance=" + balance + ", " +
            "status=" + status + ']';
    }

}
