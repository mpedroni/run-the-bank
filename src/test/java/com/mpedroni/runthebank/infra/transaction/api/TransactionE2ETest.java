package com.mpedroni.runthebank.infra.transaction.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mpedroni.runthebank.domain.account.Account;
import com.mpedroni.runthebank.domain.account.AccountGateway;
import com.mpedroni.runthebank.infra.account.persistence.AccountRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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
    AccountRepository accountRepository;

    @Autowired
    AccountGateway accountGateway;

    @BeforeEach
    void cleanDatabase () {
        accountRepository.deleteAll();
    }

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

        mvc.perform(post("/transfers")
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

    @Test
    void increaseAccountBalanceByAmountOnDeposit() throws Exception {
        var account = anAccount(1);

        mvc.perform(post("/deposits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "accountId": "%s",
                    "amount": %f
                }
                """.formatted(account.id(), 100.0)))
            .andExpect(status().isCreated());

        var updatedAccount = accountGateway.findById(account.id()).get();

        assertThat(updatedAccount.balance().floatValue()).isEqualTo(100.0f);
    }

    @Test
    void decreaseAccountBalanceByAmountOnTransfer() throws Exception {
        var payer = anAccount(1);
        var payee = anAccount(2);

        mvc.perform(post("/deposits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "accountId": "%s",
                    "amount": %f
                }
                """.formatted(payer.id(), 200.0)))
            .andExpect(status().isCreated());

        mvc.perform(post("/transfers")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "payerAccountId": "%s",
                    "payeeAccountId": "%s",
                    "amount": %f
                }
                """.formatted(payer.id(), payee.id(), 100.0)))
            .andExpect(status().isCreated());

        var balance = accountGateway.findById(payer.id()).get().balance().floatValue();

        assertThat(balance).isEqualTo(100.0f);
    }
}
