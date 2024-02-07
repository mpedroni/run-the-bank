package com.mpedroni.runthebank.infra.transaction.amqp;

import com.mpedroni.runthebank.domain.account.AccountService;
import com.mpedroni.runthebank.domain.transaction.Transaction;
import com.mpedroni.runthebank.domain.transaction.TransactionGateway;
import com.mpedroni.runthebank.domain.transaction.TransactionType;
import com.mpedroni.runthebank.domain.transaction.events.TransactionCreatedEvent;
import com.mpedroni.runthebank.infra.services.NotificationService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class TransactionListener {
    private final TransactionGateway transactionGateway;
    private final AccountService accountService;
    private final NotificationService notificationService;

    public TransactionListener(TransactionGateway transactionGateway, AccountService accountService,
        NotificationService notificationService) {
        this.transactionGateway = transactionGateway;
        this.accountService = accountService;
        this.notificationService = notificationService;
    }

    @RabbitListener(
        queues = "#{transactionCreatedQueue.name}",
        containerFactory = "rabbitListenerContainerFactory",
        concurrency = "4"
    )
    public void transactionCreated(TransactionCreatedEvent event, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) {
        try {
            var transaction = transactionGateway.findById(event.id()).orElseThrow(() -> new RuntimeException("Transaction not found. Id: " + event.id()));

            var type = transaction.type();

            if (type == TransactionType.DEPOSIT) handleDeposit(transaction);
            else if (type == TransactionType.TRANSFER) handleTransfer(transaction);
            else throw new IllegalStateException("Unknown transaction type");

            channel.basicAck(tag, false);
            notifyClients(transaction);
        } catch (Exception e) {
            System.out.println("Failed to process the message: " + e.getMessage());
        }
    }

    private void notifyClients(Transaction transaction) {
        String payeeMessage = "";
        String payerMessage = "";

        if (transaction.type() == TransactionType.DEPOSIT) {
            payeeMessage = "Seu depósito de " + transaction.amount() + " foi realizado com sucesso";
        } else {
            payeeMessage = "Você recebeu uma transferência de " + transaction.amount();
            payerMessage = "Sua transferência de " + transaction.amount() + " foi realizada com sucesso";
        }

        notificationService.notify(transaction.payee().id(), payeeMessage);

        if (!payerMessage.isEmpty()) {
            notificationService.notify(transaction.payer().id(), payerMessage);
        }
    }

    private void handleTransfer(Transaction transaction) {
        var payer = transaction.payer();
        var payerBalance = accountService.findById(payer.id()).orElseThrow().balance();

        /*
         * current transaction amount was already reserved from payer balance,
         * so by now the real payer's total balance is the current balance plus the transaction amount
         */
        var hasEnoughBalance = payerBalance.add(transaction.amount()).compareTo(transaction.amount()) >= 0;
        if (!hasEnoughBalance) {
            throw new IllegalStateException("Payer does not have enough balance");
        }

        transaction.complete();
        transactionGateway.save(transaction);
    }

    private void handleDeposit(Transaction transaction) {
        transaction.complete();
        transactionGateway.save(transaction);
    }
}
