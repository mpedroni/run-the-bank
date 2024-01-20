package com.mpedroni.runthebank.infra.transaction.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mpedroni.runthebank.domain.account.Account;
import com.mpedroni.runthebank.domain.account.AccountGateway;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class TransactionE2ETest {
    @Autowired
    MockMvc mvc;

    @Autowired
    AccountGateway accountGateway;

    Account anAccount(int number) {
        var clientId = UUID.randomUUID();
        var agency = 1234;

        var account = Account.create(
            clientId,
            agency,
            number
        );

        accountGateway.create(account);

        return account;
    }
    @Test
    void throwsWhenPayerHasNotEnoughBalance() throws Exception {
        var payer = anAccount(1);
        var payee = anAccount(2);

        mvc.perform(post("/transactions")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "payerAccountId": "%s",
                    "payeeAccountId": "%s",
                    "amount": %f
                }
                """.formatted(payer.id(), payee.id(), 100.0)))
            .andExpect(status().isConflict())
            .andExpect(content().string("Payer account does not have enough balance."));
    }
}
