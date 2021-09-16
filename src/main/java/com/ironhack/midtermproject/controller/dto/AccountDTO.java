package com.ironhack.midtermproject.controller.dto;


import java.math.BigDecimal;
import java.util.Currency;

public class AccountDTO {

    private int id;
    private Currency currency;
    private BigDecimal amount;

    public AccountDTO() {
    }

    public AccountDTO(int id, Currency currency, BigDecimal amount) {
        this.id = id;
        this.currency = currency;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
