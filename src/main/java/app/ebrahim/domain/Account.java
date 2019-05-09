package app.ebrahim.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

public class Account implements Serializable {
    private static final long serialVersionUID = 1L;


    @JsonIgnore
    private Long id;
    @JsonProperty(required = true)
    private BigDecimal balance;
    @JsonProperty(required = true)
    private String iban;
    @JsonProperty(required = true)
    private AccountStatusType accountStatusType;
    @JsonProperty(required = true)
    private String partyId;
    @JsonProperty(required = true)
    private String currencyCodeType;
    @JsonProperty(required = true)
    private String createAccountDate;
    @JsonProperty(required = true)
    private String updateAccountDate;


    public Account() {
    }

    public Account(String partyId, BigDecimal balance, String iban, String currencyCodeType, String accountStatusType, String createAccountDate, String updateAccountDate) {
        this.balance = balance;
        this.iban = iban;
        this.partyId = partyId;
        this.currencyCodeType = currencyCodeType;
        this.accountStatusType = AccountStatusType.valueOf(accountStatusType);
        this.createAccountDate = createAccountDate;
        this.updateAccountDate = updateAccountDate;
    }

    public Account(Long id, String partyId, BigDecimal balance, String iban, String currencyCodeType, String accountStatusType, String createAccountDate, String updateAccountDate) {
        this.id = id;
        this.balance = balance;
        this.iban = iban;
        this.partyId = partyId;
        this.currencyCodeType = currencyCodeType;
        this.accountStatusType = AccountStatusType.valueOf(accountStatusType);
        this.createAccountDate = createAccountDate;
        this.updateAccountDate = updateAccountDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public synchronized BigDecimal getBalance() {
        return balance;
    }

    public synchronized void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public AccountStatusType getAccountStatusType() {
        return accountStatusType;
    }

    public void setAccountStatusType(AccountStatusType accountStatusType) {
        this.accountStatusType = accountStatusType;
    }

    public synchronized String getIban() {
        return iban;
    }

    public synchronized void setIban(String iban) {
        this.iban = iban;
    }

    public String getCurrencyCodeType() {
        return currencyCodeType;
    }

    public void setCurrencyCodeType(String currencyCodeType) {
        this.currencyCodeType = currencyCodeType;
    }

    public synchronized String getCreateAccountDate() {
        return createAccountDate;
    }

    public synchronized void setCreateAccountDate(String createAccountDate) {
        this.createAccountDate = createAccountDate;
    }

    public synchronized String getUpdateAccountDate() {
        return updateAccountDate;
    }

    public synchronized void setUpdateAccountDate(String updateAccountDate) {
        this.updateAccountDate = updateAccountDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id == account.id;
    }


    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return new StringBuilder("Account{")
                .append("id=").append(id)
                .append(", partyId=").append(partyId)
                .append(", balance=").append(balance)
                .append(", iban='").append(iban).append("\'")
                .append(", currencyCodeType=").append(currencyCodeType)
                .append(", accountStatusType=").append(accountStatusType)
                .append(", createAccountDate=").append(createAccountDate)
                .append(", updateAccountDate=").append(updateAccountDate)
                .append("}").toString();
    }


    public void withdraw(BigDecimal amount) {
        this.balance = balance.subtract(amount);
    }

    public void deposit(BigDecimal amount) {
        this.balance = balance.add(amount);
    }
}
