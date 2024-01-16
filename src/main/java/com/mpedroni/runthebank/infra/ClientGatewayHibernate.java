package com.mpedroni.runthebank.infra;

import com.mpedroni.runthebank.domain.ClientGateway;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ClientGatewayHibernate implements ClientGateway {
    private final ClientRepository clientRepository;

    public ClientGatewayHibernate(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public void createCustomer(UUID id, String name, String document, String address, String password) {
        var entity = new ClientJpaEntity(id, name, document, address, password, ClientTypeJpa.CUSTOMER);
        clientRepository.save(entity);
    }
}
