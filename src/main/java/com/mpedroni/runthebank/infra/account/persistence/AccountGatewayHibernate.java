package com.mpedroni.runthebank.infra.account.persistence;

import com.mpedroni.runthebank.domain.account.Account;
import com.mpedroni.runthebank.domain.account.AccountGateway;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
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
        return accountRepository.findLastAccountNumberFrom(agency);
    }

    @Override
    public Optional<Account> findById(UUID id) {
        return accountRepository.findById(id)
            .map(accountJpaEntity -> new Account(
                accountJpaEntity.getId(),
                accountJpaEntity.getClientId(),
                accountJpaEntity.getAgency(),
                accountJpaEntity.getNumber(),
                BigDecimal.valueOf(0.0)
            ));
    }
}
