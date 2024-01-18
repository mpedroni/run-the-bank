package com.mpedroni.runthebank.infra.account.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;

@Entity(name = "Account")
@Table(name = "accounts", uniqueConstraints = {
    @UniqueConstraint(name = "accounts_agency_number_unique", columnNames = {"agency", "number"})
})
public class AccountJpaEntity {

    @Id
    private UUID id;
    private UUID clientId;
    private int agency;
    private int number;

    public AccountJpaEntity() {
    }

    public AccountJpaEntity(UUID id, UUID clientId, int agency, int number) {
        this.id = id;
        this.clientId = clientId;
        this.agency = agency;
        this.number = number;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getClientId() {
        return clientId;
    }

    public void setClientId(UUID clientId) {
        this.clientId = clientId;
    }

    public int getAgency() {
        return agency;
    }

    public void setAgency(int agency) {
        this.agency = agency;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
