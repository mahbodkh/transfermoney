package app.ebrahim.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

public class TransactionPayment implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private Long id;
    @JsonProperty(required = true)
    private BigDecimal amount;
    @JsonProperty(required = true)
    private Long sourceAccountId;
    @JsonProperty(required = true)
    private Long destinationAccountId;
    @JsonProperty(required = true)
    private String currencyCode;
    @JsonProperty(required = true)
    private String stan;
    @JsonProperty(required = true)
    private String persistenceTime;

    public TransactionPayment() {
    }

    public TransactionPayment(String currencyCode, BigDecimal amount, String stan, Long sourceAccountId, Long destinationAccountId, String persistenceTime) {
        this.amount = amount;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.currencyCode = currencyCode;
        this.stan = stan;
        this.persistenceTime = persistenceTime;
    }

    public TransactionPayment(Long id, String currencyCode, BigDecimal amount, String stan, Long sourceAccountId, Long destinationAccountId, String persistenceTime) {
        this.id = id;
        this.currencyCode = currencyCode;
        this.amount = amount;
        this.stan = stan;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.persistenceTime = persistenceTime;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public synchronized BigDecimal getAmount() {
        return amount;
    }

    public synchronized void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public synchronized Long getSourceAccountId() {
        return sourceAccountId;
    }

    public synchronized void setSourceAccountId(Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public synchronized Long getDestinationAccountId() {
        return destinationAccountId;
    }

    public synchronized void setDestinationAccountId(Long destinationAccountId) {
        this.destinationAccountId = destinationAccountId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public synchronized String getStan() {
        return stan;
    }

    public synchronized void setStan(String stan) {
        this.stan = stan;
    }

    public synchronized String getPersistenceTime() {
        return persistenceTime;
    }

    public synchronized void setPersistenceTime(String persistenceTime) {
        this.persistenceTime = persistenceTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactionPayment that = (TransactionPayment) o;
        return id == that.id &&
                Objects.equals(amount, that.amount) &&
                Objects.equals(stan, that.stan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, stan);
    }


    @Override
    public String toString() {
        return new StringBuilder("TransactionPayment{")
                .append("id=").append(id)
                .append(", amount=").append(amount)
                .append(", sourceAccountId=").append(sourceAccountId)
                .append(", destinationAccountId=").append(destinationAccountId)
                .append(", currencyCode=").append(currencyCode)
                .append(", stan='").append(stan).append('\'')
                .append(", persistenceTime=").append(persistenceTime)
                .append("}").toString();
    }

}