package com.mpedroni.runthebank.domain;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    private final AccountGateway accountGateway;

    public AccountService(AccountGateway accountGateway) {
        this.accountGateway = accountGateway;
    }

    public Account createAccountFor(UUID clientId, int agency) {
        var account = new Account(
            UUID.randomUUID(),
            clientId,
            agency,
            1,
            BigDecimal.valueOf(0.0)
        );

        accountGateway.create(account);

        return account;
    }
}
