package com.mpedroni.runthebank.infra.account.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<AccountJpaEntity, UUID> {

    @Query("SELECT COALESCE(MAX(a.number), 0) FROM Account a WHERE a.agency = ?1")
    int findLastAccountNumberFrom(int agency);
}
