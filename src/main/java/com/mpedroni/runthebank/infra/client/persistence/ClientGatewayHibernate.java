package com.mpedroni.runthebank.infra.client.persistence;

import com.mpedroni.runthebank.domain.client.Client;
import com.mpedroni.runthebank.domain.client.ClientGateway;
import org.springframework.stereotype.Service;

@Service
public class ClientGatewayHibernate implements ClientGateway {

    private final ClientRepository clientRepository;

    public ClientGatewayHibernate(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public void createClient(Client client) {
        var entity = new ClientJpaEntity(
            client.id(),
            client.name(),
            client.document(),
            client.address(),
            client.password(),
            ClientTypeJpa.fromDomain(client.type())
        );
        clientRepository.save(entity);
    }

    @Override
    public Boolean exists(String aDocument) {
        return clientRepository.findByDocument(aDocument).isPresent();
    }
}
