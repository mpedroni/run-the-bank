package com.mpedroni.runthebank.domain.client;

public interface ClientGateway {
    void createClient(Client customer);
    Boolean exists(String aDocument);
}
