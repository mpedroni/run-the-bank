package com.mpedroni.runthebank.infra.transaction.persistence;

import com.mpedroni.runthebank.domain.account.Account;
import com.mpedroni.runthebank.domain.transaction.Transaction;
import com.mpedroni.runthebank.domain.transaction.TransactionGateway;
import com.mpedroni.runthebank.domain.transaction.events.TransactionCreatedEvent;
import com.mpedroni.runthebank.infra.services.EventService;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransactionGatewayHibernate implements TransactionGateway {
    private final TransactionRepository transactionRepository;
    private final EventService eventService;

    public TransactionGatewayHibernate(TransactionRepository transactionRepository,
        EventService eventService) {
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
            transaction.type(),
            transaction.status()
        );

        transactionRepository.save(entity);
        eventService.send(new TransactionCreatedEvent(transaction.id()), "transaction.created");
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return transactionRepository.findById(id)
            .map(t -> {
                var payee = Account.restore(
                    t.getPayee().getId(),
                    t.getPayee().getClientId(),
                    t.getPayee().getAgency(),
                    t.getPayee().getNumber(),
                    BigDecimal.ZERO,
                    t.getPayee().getStatus());

                Account payer = null;
                if (t.getPayer() != null) {
                    payer = Account.restore(
                        t.getPayer().getId(),
                        t.getPayer().getClientId(),
                        t.getPayer().getAgency(),
                        t.getPayer().getNumber(),
                        BigDecimal.ZERO,
                        t.getPayer().getStatus());
                }

                return new Transaction(t.getId(), payer, payee, t.getAmount(), t.getType(),
                    t.getStatus());
            });
    }

    @Override
    public void update(Transaction transaction) {
        var entity = transactionRepository.findById(transaction
                .id())
            .orElseThrow();

        entity.setStatus(transaction.status());
        transactionRepository.save(entity);
    }
}
