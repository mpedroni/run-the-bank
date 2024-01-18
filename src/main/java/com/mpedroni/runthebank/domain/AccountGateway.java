package com.mpedroni.runthebank.domain;

public interface AccountGateway {
    void create(Account account);
    int findLastAccountNumberFrom(int agency);
}
