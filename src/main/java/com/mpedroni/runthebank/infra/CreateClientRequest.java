package com.mpedroni.runthebank.infra;

public record CreateClientRequest(
    String name,
    String document,
    String address,
    String password
) {

}
