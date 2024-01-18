package com.mpedroni.runthebank.domain;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    public Account createAccountFor(UUID clientId, int agency) {
        return new Account(
            UUID.randomUUID(),
            clientId,
            agency,
            1,
            BigDecimal.valueOf(0.0)
        );
    }
}
