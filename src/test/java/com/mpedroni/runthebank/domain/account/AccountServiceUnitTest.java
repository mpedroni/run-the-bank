package com.mpedroni.runthebank.domain.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;

public class AccountServiceUnitTest {
    AccountGateway accountGateway = mock(AccountGateway.class);

    AccountService sut = new AccountService(accountGateway);

    @Test
    void assignsAccountNumbersSequentially() {
        var anAgency = 1234;
        when(accountGateway.findLastAccountNumberFrom(anAgency)).thenReturn(1);

        var account = sut.createAccountFor(UUID.randomUUID(), anAgency);

        assertThat(account.number()).isEqualTo(2);
    }
}
