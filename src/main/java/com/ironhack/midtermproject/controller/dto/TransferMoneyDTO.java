package com.ironhack.midtermproject.controller.dto;

import java.math.BigDecimal;

public class TransferMoneyDTO {

    private long movementId;
    private BigDecimal amount;
    private int accountId;

    public TransferMoneyDTO() {
    }

    public long getMovementId() {
        return movementId;
    }

    public void setMovementId(long movementId) {
        this.movementId = movementId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
}
