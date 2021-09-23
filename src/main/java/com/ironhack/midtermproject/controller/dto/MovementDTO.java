package com.ironhack.midtermproject.controller.dto;

import com.ironhack.midtermproject.enums.MovementType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MovementDTO {

    private long id;
    private BigDecimal transferAmount;
    private BigDecimal balanceBefore;
    private BigDecimal balanceAfter;
    private MovementType movementType;
    private LocalDate orderDate;
    private LocalDateTime timeExecution;
    private LocalDate modificationDate;

    public MovementDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getTransferAmount() {
        return transferAmount;
    }

    public void setTransferAmount(BigDecimal transferAmount) {
        this.transferAmount = transferAmount;
    }

    public BigDecimal getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(BigDecimal balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public MovementType getMovementType() {
        return movementType;
    }

    public void setMovementType(MovementType movementType) {
        this.movementType = movementType;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDateTime getTimeExecution() {
        return timeExecution;
    }

    public void setTimeExecution(LocalDateTime timeExecution) {
        this.timeExecution = timeExecution;
    }

    public LocalDate getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDate modificationDate) {
        this.modificationDate = modificationDate;
    }
}
