package com.mpedroni.runthebank.infra.account.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mpedroni.runthebank.E2ETest;
import com.mpedroni.runthebank.infra.account.persistence.AccountJpaEntity;
import com.mpedroni.runthebank.infra.account.persistence.AccountRepository;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@E2ETest
class AccountE2ETest {
    @Autowired
    MockMvc mvc;

    @Autowired
    AccountRepository accountRepository;

    @BeforeEach
    void cleanDatabase() {
        accountRepository.deleteAll();
    }

    @Test
    void createsANewAccountWithTheGivenData() throws Exception {
        var aClientId = UUID.randomUUID();
        var anAgency = 1234;
        var content = """
            {
                "clientId": "%s",
                "agency": "%s"
            }
            """.formatted(aClientId, anAgency);

        mvc.perform(post("/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isCreated());

        var accounts = accountRepository.findAll();
        assertThat(accounts).hasSize(1);

        var account = accounts.stream().findFirst().orElse(null);
        assertThat(account).isNotNull();
        assertThat(account.getClientId()).isEqualTo(aClientId);
        assertThat(account.getAgency()).isEqualTo(anAgency);
        assertThat(account.getNumber()).isEqualTo(1);
    }

    @Test
    void deactivatesAnAccount() throws Exception {
        var accountId = UUID.randomUUID();
        var clientId = UUID.randomUUID();
        var account = accountRepository.save(new AccountJpaEntity(accountId, clientId, 1234, 1));

        var content = """
            {
                "accountId": "%s"
            }
            """.formatted(account.getId());

        mvc.perform(patch("/accounts/deactivate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(content))
            .andExpect(status().isNoContent());

        var deactivatedAccount = accountRepository.findById(account.getId()).orElse(null);
        assertThat(deactivatedAccount).isNotNull();
        assertThat(deactivatedAccount.isActive()).isFalse();
    }
}
