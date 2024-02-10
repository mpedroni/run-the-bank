package com.mpedroni.runthebank.infra.account.persistence;

import com.mpedroni.runthebank.domain.account.AccountStatus;
import com.mpedroni.runthebank.infra.client.persistence.ClientJpaEntity;
import com.mpedroni.runthebank.infra.transaction.persistence.TransactionJpaEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clientId", referencedColumnName = "id", insertable = false, updatable = false)
    private ClientJpaEntity client;

    @CreationTimestamp
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    @OneToMany(mappedBy = "payee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TransactionJpaEntity> earnings;
    @OneToMany(mappedBy = "payer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TransactionJpaEntity> expenses;

    public AccountJpaEntity() {
    }

    public AccountJpaEntity(UUID id, UUID clientId, int agency, int number) {
        this.id = id;
        this.clientId = clientId;
        this.agency = agency;
        this.number = number;
        this.status = AccountStatus.ACTIVE;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
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

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public List<TransactionJpaEntity> getTransactions() {
        return Stream.of(earnings, expenses)
            .flatMap(List::stream)
            .toList();
    }

    public void deactivate() {
        this.status = AccountStatus.INACTIVE;
    }

    public boolean isActive() {
        return this.status == AccountStatus.ACTIVE;
    }
}
