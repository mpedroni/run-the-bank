package com.mpedroni.runthebank.domain.account;

import java.util.Optional;
import java.util.UUID;

public interface AccountGateway {
    void create(Account account);
    void update(Account account);
    int findLastAccountNumberFrom(int agency);
    Optional<Account> findById(UUID id);
    void deactivate(Account account);
}
