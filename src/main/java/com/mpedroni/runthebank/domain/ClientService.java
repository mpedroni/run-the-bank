package com.mpedroni.runthebank.domain;

import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    private final ClientGateway clientGateway;

    public ClientService(ClientGateway clientGateway) {
        this.clientGateway = clientGateway;
    }

    public Client createCustomer(String name, String document, String address, String password) {
        var customer = new Client(UUID.randomUUID(), name, document, address, password, ClientType.CUSTOMER);
        clientGateway.createCustomer(customer);

        return customer;
    }
}
