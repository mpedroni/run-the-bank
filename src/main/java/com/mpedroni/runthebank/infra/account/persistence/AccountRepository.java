package com.mpedroni.runthebank.infra.account.persistence;

import com.mpedroni.runthebank.infra.transaction.persistence.TransactionJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<AccountJpaEntity, UUID> {

    @Query("SELECT COALESCE(MAX(a.number), 0) FROM Account a WHERE a.agency = ?1")
    int findLastAccountNumberFrom(int agency);

    @Query("SELECT t FROM Transaction t WHERE t.payerId = ?1 OR t.payeeId = ?1")
    List<TransactionJpaEntity> findTransactionsOf(UUID accountId);
}
