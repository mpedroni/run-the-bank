package com.mpedroni.runthebank.infra.account.api;

import java.util.UUID;

public record DeactivateAccountRequest(
    UUID accountId
) {

}
