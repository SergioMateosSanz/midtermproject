package com.ironhack.midtermproject.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CheckingTest {

    Checking checking;

    @BeforeEach
    void setUp() {
        checking = new Checking();
        checking.setSecretKey("123456");
    }

    @Test
    void setMinimumBalance_SetValueByDefect_InputNull() {

        checking.setMinimumBalance(null);
        assertEquals(BigDecimal.valueOf(250), checking.getMinimumBalance());
    }

    @Test
    void setMinimumBalance_SetValueByDefect_WhateverValue() {

        checking.setMinimumBalance(BigDecimal.valueOf(250));
        assertEquals(BigDecimal.valueOf(250), checking.getMinimumBalance());

        checking.setMinimumBalance(BigDecimal.valueOf(251));
        assertEquals(BigDecimal.valueOf(250), checking.getMinimumBalance());

        checking.setMinimumBalance(BigDecimal.valueOf(1));
        assertEquals(BigDecimal.valueOf(250), checking.getMinimumBalance());

        checking.setMinimumBalance(BigDecimal.valueOf(-333));
        assertEquals(BigDecimal.valueOf(250), checking.getMinimumBalance());
    }

    @Test
    void setMonthlyMaintenanceFee_SetValueByDefect_InputNull() {

        checking.setMonthlyMaintenanceFee(null);
        assertEquals(BigDecimal.valueOf(12), checking.getMonthlyMaintenanceFee());
    }

    @Test
    void setMonthlyMaintenanceFee_SetValueByDefect_WhateverValue() {

        checking.setMonthlyMaintenanceFee(BigDecimal.valueOf(1));
        assertEquals(BigDecimal.valueOf(12), checking.getMonthlyMaintenanceFee());

        checking.setMonthlyMaintenanceFee(BigDecimal.valueOf(11));
        assertEquals(BigDecimal.valueOf(12), checking.getMonthlyMaintenanceFee());

        checking.setMonthlyMaintenanceFee(BigDecimal.valueOf(-10));
        assertEquals(BigDecimal.valueOf(12), checking.getMonthlyMaintenanceFee());

        checking.setMonthlyMaintenanceFee(BigDecimal.valueOf(13));
        assertEquals(BigDecimal.valueOf(12), checking.getMonthlyMaintenanceFee());

        checking.setMonthlyMaintenanceFee(BigDecimal.valueOf(33));
        assertEquals(BigDecimal.valueOf(12), checking.getMonthlyMaintenanceFee());
    }

}