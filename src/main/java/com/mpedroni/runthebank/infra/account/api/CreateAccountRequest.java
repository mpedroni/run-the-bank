package com.mpedroni.runthebank.infra.account.api;

import java.util.UUID;

public record CreateAccountRequest(
    UUID clientId,
    int agency
) {

}
