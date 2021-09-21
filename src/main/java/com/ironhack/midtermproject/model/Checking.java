package com.ironhack.midtermproject.model;

import com.ironhack.midtermproject.enums.AccountStatus;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Entity
@DynamicUpdate
public class Checking extends Account {

    private static final BigDecimal MINIMUM_BALANCE = BigDecimal.valueOf(250);
    private static final BigDecimal MONTHLY_MAINTENANCE_FEE = BigDecimal.valueOf(12);

    @Enumerated(EnumType.STRING)
    private AccountStatus status;
    private String secretKey;
    private BigDecimal minimumBalance;
    private BigDecimal monthlyMaintenanceFee;


    public Checking() {
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public BigDecimal getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(BigDecimal minimumBalance) {

        this.minimumBalance = MINIMUM_BALANCE;
    }

    public BigDecimal getMonthlyMaintenanceFee() {
        return monthlyMaintenanceFee;
    }

    public void setMonthlyMaintenanceFee(BigDecimal monthlyMaintenanceFee) {

        this.monthlyMaintenanceFee = MONTHLY_MAINTENANCE_FEE;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
