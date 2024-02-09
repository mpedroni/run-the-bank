package com.mpedroni.runthebank.infra.account.api;

import com.mpedroni.runthebank.domain.account.AccountService;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateAccountRequest request) {
        var account = accountService.createAccountFor(
            request.clientId(),
            request.agency()
        );

        return ResponseEntity.created(URI.create(Integer.toString(account.number()))).build();
    }

    @PatchMapping("deactivate")
    public ResponseEntity<?> deactivate(@RequestBody DeactivateAccountRequest request) {
        accountService.deactivate(request.accountId());
        return ResponseEntity.noContent().build();
    }
}
