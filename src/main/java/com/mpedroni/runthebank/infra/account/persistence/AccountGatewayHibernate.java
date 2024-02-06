package com.mpedroni.runthebank.infra.account.persistence;

import com.mpedroni.runthebank.domain.account.Account;
import com.mpedroni.runthebank.domain.account.AccountGateway;
import com.mpedroni.runthebank.domain.transaction.TransactionStatus;
import com.mpedroni.runthebank.domain.transaction.TransactionType;
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
        var account = accountRepository.findById(id);

        if(account.isEmpty()) return Optional.empty();

        var balance = accountRepository.findTransactionsOf(id)
            .stream()
            .filter(transaction -> {
                var isTransfer = transaction.getType().equals(TransactionType.TRANSFER);

                return isTransfer || transaction.getStatus().equals(TransactionStatus.COMPLETED);
            })
            .map(transaction -> {
                var type = transaction.getType();
                var amount = transaction.getAmount();
                var isDeposit = type.equals(TransactionType.DEPOSIT);

                if(isDeposit) return amount;

                var isDebit = transaction.getPayerId().equals(id);
                return isDebit ? amount.negate() : amount;
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return account.map(accountJpaEntity -> new Account(
            accountJpaEntity.getId(),
            accountJpaEntity.getClientId(),
            accountJpaEntity.getAgency(),
            accountJpaEntity.getNumber(),
            balance
        ));
    }
}
