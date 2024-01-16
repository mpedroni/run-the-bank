package com.mpedroni.runthebank.domain;

public interface ClientGateway {
    void createCustomer(Client customer);

    Boolean exists(String aDocument);
}
