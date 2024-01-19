package com.mpedroni.runthebank.domain.account;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mpedroni.runthebank.domain.ValidationError;
import java.util.UUID;
import org.junit.jupiter.api.Test;

public class AccountServiceUnitTest {
    AccountGateway accountGateway = mock(AccountGateway.class);

    AccountService sut = new AccountService(accountGateway);

    @Test
    void createsAnAccountForTheGivenClient() {
        var anAgency = 1234;
        var aClientId = UUID.randomUUID();

        var account = sut.createAccountFor(aClientId, anAgency);

        verify(accountGateway).create(account);

        assertThat(account.clientId()).isEqualTo(aClientId);
        assertThat(account.agency()).isEqualTo(anAgency);
        assertThat(account.number()).isEqualTo(1);
    }

    @Test
    void assignsAccountNumbersSequentially() {
        var anAgency = 1234;
        when(accountGateway.findLastAccountNumberFrom(anAgency)).thenReturn(1);

        var account = sut.createAccountFor(UUID.randomUUID(), anAgency);

        assertThat(account.number()).isEqualTo(2);
    }

    @Test
    void throwsWhenTheGivenAgencyDoesNotExist() {
        var anInvalidAgency = 0;

        assertThatThrownBy(() -> sut.createAccountFor(UUID.randomUUID(), anInvalidAgency))
            .isInstanceOf(ValidationError.class)
            .hasMessage("The agency does not exists.");
    }
}
