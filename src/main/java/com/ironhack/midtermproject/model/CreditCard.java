package com.ironhack.midtermproject.model;

import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
public class CreditCard extends Account {

    private static final BigDecimal MINIMUM_INTEREST_RATE = BigDecimal.valueOf(0.1);
    private static final BigDecimal DEFAULT_INTEREST_RATE = BigDecimal.valueOf(0.2);
    private static final BigDecimal MAXIMUM_CREDIT_LIMIT = BigDecimal.valueOf(100000);
    private static final BigDecimal DEFAULT_CREDIT_LIMIT = BigDecimal.valueOf(100);

    private BigDecimal creditLimit;
    private BigDecimal interestRate;

    public CreditCard() {
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(BigDecimal creditLimit) {
        if (creditLimit == null) {
            this.creditLimit = DEFAULT_CREDIT_LIMIT;
        } else {
            if (creditLimit.compareTo(MAXIMUM_CREDIT_LIMIT) == 1) {
                this.creditLimit = MAXIMUM_CREDIT_LIMIT;
            } else {
                if (creditLimit.compareTo(BigDecimal.valueOf(0)) == -1) {
                    this.creditLimit = BigDecimal.ZERO;
                } else {
                    this.creditLimit = creditLimit;
                }
            }
        }
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {

        if (interestRate == null) {
            this.interestRate = DEFAULT_INTEREST_RATE;
        } else {
            if (interestRate.compareTo(MINIMUM_INTEREST_RATE) == -1) {
                this.interestRate = MINIMUM_INTEREST_RATE;
            } else {
                this.interestRate = interestRate;
            }
        }
    }
}
