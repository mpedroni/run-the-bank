package com.mpedroni.runthebank.domain.client;

import com.mpedroni.runthebank.domain.ApplicationException;
import com.mpedroni.runthebank.domain.ValidationError;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    private final ClientGateway clientGateway;

    public ClientService(ClientGateway clientGateway) {
        this.clientGateway = clientGateway;
    }

    public Client createCustomer(String name, String document, String address, String password) {
        if (document == null || document.length() != 11) {
            throw new ValidationError("CPF must have 11 digits");
        }

        return createClient(name, document, address, password, ClientType.CUSTOMER);
    }

    public Client createCompany(String name, String document, String address, String password) {
        if (document == null || document.length() != 14) {
            throw new ValidationError("CNPJ must have 14 digits");
        }

        return createClient(name, document, address, password, ClientType.COMPANY);
    }

    private Client createClient(String name, String document, String address, String password, ClientType type) {
        if (name == null || name.isBlank()) {
            throw new ValidationError("Name must not be empty");
        }
        if (address == null || address.isBlank()) {
            throw new ValidationError("Address must not be empty");
        }
        if (password == null || password.isBlank()) {
            throw new ValidationError("Password must not be empty");
        }

        if (clientGateway.exists(document)) {
            throw new ApplicationException(
                String.format("A %s with the given document already exists", type.name().toLowerCase())
            );
        }

        var client = new Client(UUID.randomUUID(), name, document, address, password,
            type);

        clientGateway.createClient(client);

        return client;
    }
}
