package com.ironhack.midtermproject.model;

import com.ironhack.midtermproject.classes.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SavingTest {

    Saving saving;
    Money money;

    @BeforeEach
    void setUp() {

        saving = new Saving();
    }

    @Test
    void setMinimumBalance_SetDefaultValue_NoInputValue() {

        saving.setMinimumBalance(null);
        assertEquals(BigDecimal.valueOf(1000), saving.getMinimumBalance());
    }

    @Test
    void setMinimumBalance_SetMinimumValue_InputValueLessThanMinimum() {

        saving.setMinimumBalance(BigDecimal.valueOf(99));
        assertEquals(BigDecimal.valueOf(100), saving.getMinimumBalance());

        saving.setMinimumBalance(BigDecimal.valueOf(0));
        assertEquals(BigDecimal.valueOf(100), saving.getMinimumBalance());

        saving.setMinimumBalance(BigDecimal.valueOf(-3));
        assertEquals(BigDecimal.valueOf(100), saving.getMinimumBalance());
    }

    @Test
    void setMinimumBalance_SetInputValue_InputValueGreaterThanMinimum() {

        saving.setMinimumBalance(BigDecimal.valueOf(101));
        assertEquals(BigDecimal.valueOf(101), saving.getMinimumBalance());

        saving.setMinimumBalance(BigDecimal.valueOf(444));
        assertEquals(BigDecimal.valueOf(444), saving.getMinimumBalance());

        saving.setMinimumBalance(BigDecimal.valueOf(1500));
        assertEquals(BigDecimal.valueOf(1500), saving.getMinimumBalance());
    }

    @Test
    void setInterestRate_SetMinimumValue_InputValueLessThanMinimum() {

        saving.setInterestRate(BigDecimal.valueOf(0.0024));
        assertEquals(BigDecimal.valueOf(0.0025), saving.getInterestRate());

        saving.setInterestRate(BigDecimal.valueOf(0.0001));
        assertEquals(BigDecimal.valueOf(0.0025), saving.getInterestRate());

        saving.setInterestRate(BigDecimal.valueOf(-0.6));
        assertEquals(BigDecimal.valueOf(0.0025), saving.getInterestRate());
    }

    @Test
    void setInterestRate_SetMaximumValue_InputValueGreaterThanMaximum() {

        saving.setInterestRate(BigDecimal.valueOf(0.51));
        assertEquals(BigDecimal.valueOf(0.5), saving.getInterestRate());

        saving.setInterestRate(BigDecimal.valueOf(1.1));
        assertEquals(BigDecimal.valueOf(0.5), saving.getInterestRate());

        saving.setInterestRate(BigDecimal.valueOf(33));
        assertEquals(BigDecimal.valueOf(0.5), saving.getInterestRate());
    }

    @Test
    void setInterestRate_SetInputValue_InputValueBetweenMinimumAndMaximum() {

        saving.setInterestRate(BigDecimal.valueOf(0.0025));
        assertEquals(BigDecimal.valueOf(0.0025), saving.getInterestRate());

        saving.setInterestRate(BigDecimal.valueOf(0.3));
        assertEquals(BigDecimal.valueOf(0.3), saving.getInterestRate());

        saving.setInterestRate(BigDecimal.valueOf(0.5));
        assertEquals(BigDecimal.valueOf(0.5), saving.getInterestRate());
    }
}