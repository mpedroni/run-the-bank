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
    public void update(Account account) {
        var entity = accountRepository.findById(account.id()).orElseThrow();
        entity.setStatus(account.status());
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
                var isCancelled = transaction.getStatus().equals(TransactionStatus.CANCELED);
                if (isCancelled) return false;

                var isTransfer = transaction.getType().equals(TransactionType.TRANSFER);
                var isCompleted = transaction.getStatus().equals(TransactionStatus.COMPLETED);

                /*
                * Transfers are considered even when are pending to avoid negative balance.
                * Deposits are considered only when completed.
                * */
                return isTransfer || isCompleted;
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

        return account.map(accountJpaEntity -> Account.restore(
            accountJpaEntity.getId(),
            accountJpaEntity.getClientId(),
            accountJpaEntity.getAgency(),
            accountJpaEntity.getNumber(),
            balance,
            accountJpaEntity.getStatus()
        ));
    }

    @Override
    public void deactivate(Account account) {
        var entity = accountRepository.findById(account.id()).orElseThrow();
        entity.deactivate();
        accountRepository.save(entity);
    }
}
