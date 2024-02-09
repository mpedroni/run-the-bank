package com.mpedroni.runthebank.infra.transaction.persistence;

import com.mpedroni.runthebank.domain.transaction.TransactionStatus;
import com.mpedroni.runthebank.domain.transaction.TransactionType;
import com.mpedroni.runthebank.infra.account.persistence.AccountJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity(name = "Transaction")
@Table(name = "transactions")
public class TransactionJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = true)
    private UUID payerId;

    private UUID payeeId;

    @ManyToOne(optional = true)
    @JoinColumn(name = "payerId", referencedColumnName = "id", insertable = false, updatable = false)
    private AccountJpaEntity payer;

    @ManyToOne
    @JoinColumn(name = "payeeId", referencedColumnName = "id", insertable = false, updatable = false)
    private AccountJpaEntity payee;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @CreationTimestamp
    private Instant createdAt;
    @UpdateTimestamp
    private Instant updatedAt;

    public TransactionJpaEntity() {
    }

    public TransactionJpaEntity(UUID id, UUID payerId, UUID payeeId, BigDecimal amount,
        TransactionType type, TransactionStatus status) {
        this.id = id;
        this.payerId = payerId;
        this.payeeId = payeeId;
        this.amount = amount;
        this.type = type;
        this.status = status;
    }

    public static TransactionJpaEntity transferOf(UUID payerId, UUID payeeId, BigDecimal amount) {
        return new TransactionJpaEntity(UUID.randomUUID(), payerId, payeeId, amount,
            TransactionType.TRANSFER, TransactionStatus.COMPLETED);
    }

    public static TransactionJpaEntity depositOf(AccountJpaEntity account, BigDecimal amount,
        TransactionStatus status) {
        return new TransactionJpaEntity(UUID.randomUUID(), null, account.getId(), amount,
            TransactionType.DEPOSIT, status);
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

    public UUID getPayerId() {
        return payerId;
    }

    public void setPayerId(UUID payerId) {
        this.payerId = payerId;
    }

    public UUID getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(UUID payeeId) {
        this.payeeId = payeeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public AccountJpaEntity getPayer() {
        return payer;
    }

    public void setPayer(AccountJpaEntity payer) {
        this.payer = payer;
    }

    public AccountJpaEntity getPayee() {
        return payee;
    }

    public void setPayee(AccountJpaEntity payee) {
        this.payee = payee;
    }
}
