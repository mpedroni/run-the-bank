package com.mpedroni.runthebank.infra.transaction.api;

import com.mpedroni.runthebank.domain.account.AccountService;
import com.mpedroni.runthebank.domain.transaction.TransactionService;
import java.math.BigDecimal;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    private final TransactionService transactionService;
    private final AccountService accountService;

    public TransactionController(TransactionService transactionService,
        AccountService clientService) {
        this.transactionService = transactionService;
        this.accountService = clientService;
    }

    static URI locationFor(UUID id) {
        return URI.create("transactions/" + id);
    }

    @PostMapping("transfers")
    public ResponseEntity<?> transfer(@RequestBody CreateTransferRequest request) {
        var payer = accountService.findById(request.payerAccountId());
        if (payer.isEmpty()) {
            return ResponseEntity.badRequest().body("Payer account does not exists.");
        }

        var payee = accountService.findById(request.payeeAccountId());
        if (payee.isEmpty()) {
            return ResponseEntity.badRequest().body("Payee account does not exists.");
        }

        var transfer = transactionService.transfer(payer.get(), payee.get(),
            BigDecimal.valueOf(request.amount()));

        return ResponseEntity.created(locationFor(transfer.id())).build();
    }

    @PostMapping("deposits")
    public ResponseEntity<?> deposit(@RequestBody CreateDepositRequest request) {
        var account = accountService.findById(request.accountId());

        if (account.isEmpty()) {
            return ResponseEntity.badRequest().body("Account does not exists.");
        }

        var deposit = transactionService.deposit(account.get(),
            BigDecimal.valueOf(request.amount()));

        return ResponseEntity.created(locationFor(deposit.id())).build();
    }

    @PatchMapping("transactions/cancel")
    public ResponseEntity<?> cancel(@RequestBody CancelTransactionRequest request) {
        transactionService.cancel(request.transactionId());
        return ResponseEntity.noContent().build();
    }
}
