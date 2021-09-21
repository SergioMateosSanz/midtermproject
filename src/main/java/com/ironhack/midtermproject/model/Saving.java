package com.ironhack.midtermproject.model;

import com.ironhack.midtermproject.enums.AccountStatus;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.math.BigDecimal;

@Entity
@DynamicUpdate
public class Saving extends Account {

    private static final BigDecimal MINIMUM_INTEREST_RATE = BigDecimal.valueOf(0.0025);
    private static final BigDecimal MAXIMUM_INTEREST_RATE = BigDecimal.valueOf(0.5);
    private static final BigDecimal MINIMUM_BALANCE = BigDecimal.valueOf(100);
    private static final BigDecimal DEFAULT_MINIMUM_BALANCE = BigDecimal.valueOf(1000);

    private String secretKey;
    private BigDecimal minimumBalance;
    private BigDecimal interestRate;
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    public Saving() {
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
        if (minimumBalance == null) {
            this.minimumBalance = DEFAULT_MINIMUM_BALANCE;
        } else {
            if (minimumBalance.compareTo(MINIMUM_BALANCE) == -1) {
                this.minimumBalance = MINIMUM_BALANCE;
            } else {
                this.minimumBalance = minimumBalance;
            }
        }
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {

        switch (interestRate.compareTo(MINIMUM_INTEREST_RATE)) {
            case -1:
            case 0:
                this.interestRate = MINIMUM_INTEREST_RATE;
                break;
            case 1:
                if (interestRate.compareTo(MAXIMUM_INTEREST_RATE) == 1){
                this.interestRate = MAXIMUM_INTEREST_RATE;
            } else{
                this.interestRate = interestRate;
            }
        }
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
