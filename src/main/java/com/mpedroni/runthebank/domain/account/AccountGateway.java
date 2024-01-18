package com.mpedroni.runthebank.domain.account;

public interface AccountGateway {
    void create(Account account);
    int findLastAccountNumberFrom(int agency);
}
