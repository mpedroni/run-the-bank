package com.mpedroni.runthebank.infra;

import java.util.UUID;

public record CreateAccountRequest(
    UUID clientId,
    int agency
) {

}
