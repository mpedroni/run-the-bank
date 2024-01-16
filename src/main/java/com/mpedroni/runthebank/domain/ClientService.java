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
        if(name == null || name.isBlank()) throw new ValidationError("Name must not be empty");
        if(document == null || document.length() != 11) throw new ValidationError("Document must have 11 digits");
        if(address == null || address.isBlank()) throw new ValidationError("Address must not be empty");
        if(password == null || password.isBlank()) throw new ValidationError("Password must not be empty");

        var customer = new Client(UUID.randomUUID(), name, document, address, password, ClientType.CUSTOMER);
        clientGateway.createCustomer(customer);

        return customer;
    }
}
