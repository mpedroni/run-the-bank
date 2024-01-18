package com.mpedroni.runthebank.infra;

import com.mpedroni.runthebank.domain.Account;
import com.mpedroni.runthebank.domain.AccountGateway;
import org.springframework.stereotype.Service;

@Service
public class AccountGatewayHibernate implements AccountGateway {
    private final AccountRepository accountRepository;

    public AccountGatewayHibernate(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public void create(Account account) {
        var entity = new AccountJpaEntity(
            account.id(),
            account.clientId(),
            account.agency(),
            account.number()
        );

        accountRepository.save(entity);
    }

    @Override
    public int findLastAccountNumberFrom(int agency) {
        return 0;
    }
}
