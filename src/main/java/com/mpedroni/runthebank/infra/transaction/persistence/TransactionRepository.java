package com.mpedroni.runthebank.infra.transaction.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionJpaEntity, UUID> {
}
