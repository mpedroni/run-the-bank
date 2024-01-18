package com.mpedroni.runthebank.infra.client.api;

public record CreateClientRequest(
    String name,
    String document,
    String address,
    String password
) {

}
