package com.mpedroni.runthebank.infra.account.persistence;

import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<AccountJpaEntity, UUID> {

    @Query("SELECT COALESCE(MAX(a.number), 0) FROM Account a WHERE a.agency = ?1")
    int findLastAccountNumberFrom(int agency);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.payeeId = ?1 OR t.payerId = ?1")
    BigDecimal getBalanceOf(UUID accountId);
}
