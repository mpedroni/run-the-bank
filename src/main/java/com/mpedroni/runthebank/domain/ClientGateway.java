package com.mpedroni.runthebank.domain;

public interface ClientGateway {
    void createClient(Client customer);
    Boolean exists(String aDocument);
}
