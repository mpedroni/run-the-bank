package com.mpedroni.runthebank.infra.transaction.persistence;

import com.mpedroni.runthebank.domain.transaction.TransactionType;
import com.mpedroni.runthebank.domain.transaction.TransactionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity(name = "Transaction")
@Table(name = "transactions")
public class TransactionJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = true)
    private UUID payerId;
    private UUID payeeId;
    private BigDecimal amount;

    private TransactionType type;

    private TransactionStatus status;

    public TransactionJpaEntity() {
    }

    public TransactionJpaEntity(UUID id, UUID payerId, UUID payeeId, BigDecimal amount, TransactionType type) {
        this.id = id;
        this.payerId = payerId;
        this.payeeId = payeeId;
        this.amount = amount;
        this.type = type;
        this.status = TransactionStatus.PENDING;
    }

    public static TransactionJpaEntity transferOf(UUID payerId, UUID payeeId, BigDecimal amount) {
        return new TransactionJpaEntity(UUID.randomUUID(), payerId, payeeId, amount, TransactionType.TRANSFER);
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
}
