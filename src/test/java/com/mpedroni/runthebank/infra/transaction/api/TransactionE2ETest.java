package com.mpedroni.runthebank.infra.transaction.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mpedroni.runthebank.E2ETest;
import com.mpedroni.runthebank.domain.account.Account;
import com.mpedroni.runthebank.domain.account.AccountGateway;
import com.mpedroni.runthebank.domain.transaction.TransactionStatus;
import com.mpedroni.runthebank.infra.account.persistence.AccountRepository;
import com.mpedroni.runthebank.infra.client.persistence.ClientJpaEntity;
import com.mpedroni.runthebank.infra.client.persistence.ClientRepository;
import com.mpedroni.runthebank.infra.client.persistence.ClientTypeJpa;
import com.mpedroni.runthebank.infra.transaction.persistence.TransactionRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@E2ETest
class TransactionE2ETest {
    private static ClientJpaEntity john;
    private static ClientJpaEntity jane;

    @Autowired
    MockMvc mvc;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    AccountGateway accountGateway;

    @BeforeAll
    static void setUp(@Autowired ClientRepository clientRepository) {
        john = new ClientJpaEntity(UUID.randomUUID(), "John Doe", "12345678900", "Address", "password", ClientTypeJpa.CUSTOMER);
        jane = new ClientJpaEntity(UUID.randomUUID(), "Jane Doe", "12345678901", "Address", "password", ClientTypeJpa.CUSTOMER);
        clientRepository.saveAll(List.of(john, jane));
    }


    @BeforeEach
    void cleanDatabase() {
        accountRepository.deleteAll();
    }

    Account anAccount(int number, ClientJpaEntity client) {
        var agency = 1234;

        var account = Account.create(
            client.getId(),
            agency,
            number
        );

        accountGateway.create(account);

        return account;
    }

    void aDepositOf(UUID accountId, float amount) throws Exception {
        mvc.perform(post("/deposits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "accountId": "%s",
                        "amount": %f
                    }
                    """.formatted(accountId, amount)))
            .andExpect(status().isCreated());
    }

    ResultActions aTransferWithResultsOf(UUID payerId, UUID payeeId, float amount)
        throws Exception {
        return mvc.perform(post("/transfers")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "payerAccountId": "%s",
                    "payeeAccountId": "%s",
                    "amount": %f
                }
                """.formatted(payerId, payeeId, amount)));
    }

    @Test
    void createsAPendingTransaction() throws Exception {
        var account = anAccount(1, john);

        aDepositOf(account.id(), 100);

        var transaction = transactionRepository.findAll().get(0);

        assertThat(transaction.getAmount().floatValue()).isEqualTo(100.0f);
        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.PENDING);
    }

    @Test
    void cancelsATransaction() throws Exception {
        var account = anAccount(1, john);

        aDepositOf(account.id(), 100);

        var depositId = transactionRepository.findAll().getFirst().getId();

        mvc.perform(patch("/transactions/cancel")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "transactionId": "%s"
                    }
                    """.formatted(depositId)))
            .andExpect(status().isNoContent());

        var deposit = transactionRepository.findById(depositId).orElseThrow();
        assertThat(deposit.getStatus()).isEqualTo(TransactionStatus.CANCELED);
    }

    @Test
    void throwsWhenPayerHasNotEnoughBalance() throws Exception {
        var payer = anAccount(1, john);
        var payee = anAccount(2, jane);

        aTransferWithResultsOf(payer.id(), payee.id(), 100)
            .andExpect(status().isConflict())
            .andExpect(content().string("Payer account does not have enough balance."));
    }

    @Test
    void throwsWhenPayerIsInactive() throws Exception {
        var payer = anAccount(1, john);
        var payee = anAccount(2, jane);

        accountGateway.deactivate(payer);

        aTransferWithResultsOf(payer.id(), payee.id(), 100)
            .andExpect(status().isConflict())
            .andExpect(content().string("Payer account is not active."));
    }

    @Test
    void throwsWhenPayeeIsInactive() throws Exception {
        var payer = anAccount(1, john);
        var payee = anAccount(2, jane);

        accountGateway.deactivate(payee);

        aTransferWithResultsOf(payer.id(), payee.id(), 100)
            .andExpect(status().isConflict())
            .andExpect(content().string("Payee account is not active."));
    }
}
