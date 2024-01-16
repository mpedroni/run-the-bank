package com.mpedroni.runthebank.infra;

public record CreateCustomerRequest(
    String name,
    String document,
    String address,
    String password
) {

}
