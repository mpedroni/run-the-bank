package com.mpedroni.runthebank.infra;

import com.mpedroni.runthebank.domain.Client;
import com.mpedroni.runthebank.domain.ClientGateway;
import org.springframework.stereotype.Service;

@Service
public class ClientGatewayHibernate implements ClientGateway {

    private final ClientRepository clientRepository;

    public ClientGatewayHibernate(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public void createCustomer(Client customer) {
        var entity = new ClientJpaEntity(
            customer.id(),
            customer.name(),
            customer.document(),
            customer.address(),
            customer.password(),
            ClientTypeJpa.fromDomain(customer.type())
        );
        clientRepository.save(entity);
    }

    @Override
    public void createCompany(Client company) {
        var entity = new ClientJpaEntity(
            company.id(),
            company.name(),
            company.document(),
            company.address(),
            company.password(),
            ClientTypeJpa.fromDomain(company.type())
        );

        clientRepository.save(entity);
    }

    @Override
    public Boolean exists(String aDocument) {
        return clientRepository.findByDocument(aDocument).isPresent();
    }
}
