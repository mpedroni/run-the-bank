package com.mpedroni.runthebank.infra.transaction.persistence;

import com.mpedroni.runthebank.domain.transaction.Transaction;
import com.mpedroni.runthebank.domain.transaction.TransactionGateway;
import com.mpedroni.runthebank.domain.transaction.events.TransactionCreatedEvent;
import com.mpedroni.runthebank.infra.services.EventService;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionGatewayHibernate implements TransactionGateway {
    private final TransactionRepository transactionRepository;
    private final EventService eventService;

    public TransactionGatewayHibernate(TransactionRepository transactionRepository, EventService eventService) {
        this.transactionRepository = transactionRepository;
        this.eventService = eventService;
    }

    @Override
    @Transactional
    public void create(Transaction transaction) {
        var payer = transaction.payer();
        var payerId = payer == null ? null : payer.id();

        var entity = new TransactionJpaEntity(
            transaction.id(),
            payerId,
            transaction.payee().id(),
            transaction.amount(),
            transaction.type()
        );

        transactionRepository.save(entity);
        eventService.send(new TransactionCreatedEvent(transaction.id()), "transaction.created");
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return transactionRepository.findById(id)
            .map(entity -> new Transaction(
                entity.getId(),
                null,
                null,
                entity.getAmount(),
                entity.getType(),
                entity.getStatus()
            ));
    }
}
