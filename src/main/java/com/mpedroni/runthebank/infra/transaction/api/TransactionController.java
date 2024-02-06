package com.mpedroni.runthebank.infra.transaction.api;

import com.mpedroni.runthebank.domain.account.AccountService;
import com.mpedroni.runthebank.domain.transaction.TransactionService;
import java.math.BigDecimal;
import java.net.URI;
import org.springframework.http.ResponseEntity;
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

        var transaction = transactionService.transfer(payer.get(), payee.get(),
            BigDecimal.valueOf(request.amount()));

        return ResponseEntity.created(URI.create("/transactions/" + transaction.id())).build();
    }

    @PostMapping("deposits")
    public ResponseEntity<?> deposit(@RequestBody CreateDepositRequest request) {
        var account = accountService.findById(request.accountId());

        if (account.isEmpty()) {
            return ResponseEntity.badRequest().body("Account does not exists.");
        }

        var transaction = transactionService.deposit(account.get(),
            BigDecimal.valueOf(request.amount()));

        return ResponseEntity.created(URI.create("/transactions/" + transaction.id())).build();
    }
}
