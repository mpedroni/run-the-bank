package com.mpedroni.runthebank.domain;

public interface ClientGateway {
    void createCustomer(Client customer);
    void createCompany(Client company);
    Boolean exists(String aDocument);
}
