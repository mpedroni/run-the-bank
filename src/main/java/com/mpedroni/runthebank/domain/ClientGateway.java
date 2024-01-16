package com.mpedroni.runthebank.domain;

import java.util.UUID;

public interface ClientGateway {
    void createCustomer(UUID id, String name, String document, String address, String password);
}
