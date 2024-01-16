package com.mpedroni.runthebank.infra;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<ClientJpaEntity, UUID> {

    Optional<ClientJpaEntity> findByDocument(String aDocument);
}
