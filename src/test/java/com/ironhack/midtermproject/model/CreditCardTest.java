package com.ironhack.midtermproject.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CreditCardTest {

    CreditCard creditCard;

    @BeforeEach
    void setUp() {
        creditCard = new CreditCard();
    }

    @Test
    void setCreditLimit_SetDefaultValue_NoInputValue() {

        creditCard.setCreditLimit(null);
        assertEquals(BigDecimal.valueOf(100), creditCard.getCreditLimit());
    }

    @Test
    void setCreditLimit_SetInputValue_InputValueLessThanDefault() {

        creditCard.setCreditLimit(BigDecimal.valueOf(99));
        assertEquals(BigDecimal.valueOf(99), creditCard.getCreditLimit());

        creditCard.setCreditLimit(BigDecimal.valueOf(0));
        assertEquals(BigDecimal.valueOf(0), creditCard.getCreditLimit());
    }

    @Test
    void setCreditLimit_SetZeroValue_InputValueNegative() {

        creditCard.setCreditLimit(BigDecimal.valueOf(-1));
        assertEquals(BigDecimal.valueOf(0), creditCard.getCreditLimit());

        creditCard.setCreditLimit(BigDecimal.valueOf(-32));
        assertEquals(BigDecimal.valueOf(0), creditCard.getCreditLimit());
    }

    @Test
    void setCreditLimit_SetInputValue_InputValueLessThanMaximum() {

        creditCard.setCreditLimit(BigDecimal.valueOf(101));
        assertEquals(BigDecimal.valueOf(101), creditCard.getCreditLimit());

        creditCard.setCreditLimit(BigDecimal.valueOf(444));
        assertEquals(BigDecimal.valueOf(444), creditCard.getCreditLimit());

        creditCard.setCreditLimit(BigDecimal.valueOf(15000));
        assertEquals(BigDecimal.valueOf(15000), creditCard.getCreditLimit());
    }

    @Test
    void setCreditLimit_SetMaximumValue_InputValueGreaterThanMaximum() {

        creditCard.setCreditLimit(BigDecimal.valueOf(100001));
        assertEquals(BigDecimal.valueOf(100000), creditCard.getCreditLimit());

        creditCard.setCreditLimit(BigDecimal.valueOf(44444444));
        assertEquals(BigDecimal.valueOf(100000), creditCard.getCreditLimit());

        creditCard.setCreditLimit(BigDecimal.valueOf(150000));
        assertEquals(BigDecimal.valueOf(100000), creditCard.getCreditLimit());
    }

    @Test
    void setInterestRate_SetDefaultValue_InputValueNull() {

        creditCard.setInterestRate(null);
        assertEquals(BigDecimal.valueOf(0.2), creditCard.getInterestRate());
    }

    @Test
    void setInterestRate_SetMinimumValue_InputValueLessThanMinimum() {

        creditCard.setInterestRate(BigDecimal.valueOf(0.0024));
        assertEquals(BigDecimal.valueOf(0.1), creditCard.getInterestRate());

        creditCard.setInterestRate(BigDecimal.valueOf(0.09));
        assertEquals(BigDecimal.valueOf(0.1), creditCard.getInterestRate());

        creditCard.setInterestRate(BigDecimal.valueOf(-0.6));
        assertEquals(BigDecimal.valueOf(0.1), creditCard.getInterestRate());
    }

    @Test
    void setInterestRate_SetInputValue_InputValueGreaterThanMinimum() {

        creditCard.setInterestRate(BigDecimal.valueOf(0.11));
        assertEquals(BigDecimal.valueOf(0.11), creditCard.getInterestRate());

        creditCard.setInterestRate(BigDecimal.valueOf(0.3));
        assertEquals(BigDecimal.valueOf(0.3), creditCard.getInterestRate());

        creditCard.setInterestRate(BigDecimal.valueOf(0.5));
        assertEquals(BigDecimal.valueOf(0.5), creditCard.getInterestRate());
    }
}