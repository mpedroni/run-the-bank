package com.mpedroni.runthebank.infra.transaction.persistence;

import com.mpedroni.runthebank.domain.transaction.Transaction;
import com.mpedroni.runthebank.domain.transaction.TransactionGateway;
import org.springframework.stereotype.Service;

@Service
public class TransactionGatewayHibernate implements TransactionGateway {
    private final TransactionRepository transactionRepository;

    public TransactionGatewayHibernate(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public void create(Transaction transaction) {
        var entity = new TransactionJpaEntity(
            transaction.id(),
            transaction.payer().id(),
            transaction.payee().id(),
            transaction.amount()
        );

        transactionRepository.save(entity);
    }
}
