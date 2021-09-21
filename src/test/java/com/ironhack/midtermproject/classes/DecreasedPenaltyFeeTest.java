package com.ironhack.midtermproject.classes;

import com.ironhack.midtermproject.enums.AccountStatus;
import com.ironhack.midtermproject.model.Checking;
import com.ironhack.midtermproject.model.Saving;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DecreasedPenaltyFeeTest {

    @Autowired
    DecreasedPenaltyFee decreasedPenaltyFee;

    private Saving saving;
    private Checking checking;

    @BeforeEach
    void setUp() {
        saving = new Saving();
        saving.setBalance(new Money(BigDecimal.valueOf(1000)));
        saving.setMinimumBalance(BigDecimal.valueOf(100));
        saving.setPenaltyFee(BigDecimal.valueOf(40));
        saving.setStatus(AccountStatus.ACTIVE);

        checking = new Checking();
        checking.setBalance(new Money(BigDecimal.valueOf(1000)));
        checking.setMinimumBalance(BigDecimal.valueOf(250));
        checking.setPenaltyFee(BigDecimal.valueOf(40));
        checking.setMonthlyMaintenanceFee(BigDecimal.valueOf(12));
        checking.setStatus(AccountStatus.ACTIVE);
    }


    @Test
    void isPenaltyFeeSavingAccounts_ReturnFalse_NotUnderMinimumBalance() {

        assertFalse(decreasedPenaltyFee.isPenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(1)));
        assertFalse(decreasedPenaltyFee.isPenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(10)));
        assertFalse(decreasedPenaltyFee.isPenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(300)));
        assertFalse(decreasedPenaltyFee.isPenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(600)));
    }

    @Test
    void isPenaltyFeeSavingAccounts_ReturnTrue_UnderMinimumBalance() {

        assertTrue(decreasedPenaltyFee.isPenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(901)));
        assertTrue(decreasedPenaltyFee.isPenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(910)));
        assertTrue(decreasedPenaltyFee.isPenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(999)));
    }


    @Test
    void isPenaltyFeeCheckingAccounts_ReturnFalse_NotUnderMinimumBalance() {

        assertFalse(decreasedPenaltyFee.isPenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(1)));
        assertFalse(decreasedPenaltyFee.isPenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(10)));
        assertFalse(decreasedPenaltyFee.isPenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(300)));
        assertFalse(decreasedPenaltyFee.isPenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(600)));
    }


    @Test
    void isPenaltyFeeCheckingAccounts_ReturnTrue_UnderMinimumBalance() {

        assertTrue(decreasedPenaltyFee.isPenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(751)));
        assertTrue(decreasedPenaltyFee.isPenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(910)));
        assertTrue(decreasedPenaltyFee.isPenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(999)));
    }

    @Test
    void calculateBalanceAmountToSet_CorrectCalculation_ActualAmountPositive() {

        BigDecimal result = BigDecimal.valueOf(-11);
        assertEquals(result, decreasedPenaltyFee.calculateBalanceAmountToSet(BigDecimal.valueOf(1), BigDecimal.valueOf(12)));

        result = BigDecimal.valueOf(100);
        assertEquals(result, decreasedPenaltyFee.calculateBalanceAmountToSet(BigDecimal.valueOf(112), BigDecimal.valueOf(12)));

        result = BigDecimal.valueOf(443.87);
        assertEquals(result, decreasedPenaltyFee.calculateBalanceAmountToSet(BigDecimal.valueOf(455.87), BigDecimal.valueOf(12)));
    }

    @Test
    void calculateBalanceAmountToSet_CorrectCalculation_ActualAmountZero() {

        BigDecimal result = BigDecimal.valueOf(-12);
        assertEquals(result, decreasedPenaltyFee.calculateBalanceAmountToSet(BigDecimal.ZERO, BigDecimal.valueOf(12)));

        result = BigDecimal.valueOf(-100);
        assertEquals(result, decreasedPenaltyFee.calculateBalanceAmountToSet(BigDecimal.ZERO, BigDecimal.valueOf(100)));
    }

    @Test
    void calculateBalanceAmountToSet_CorrectCalculation_ActualAmountNegative() {

        BigDecimal result = BigDecimal.valueOf(-13);
        assertEquals(result, decreasedPenaltyFee.calculateBalanceAmountToSet(BigDecimal.valueOf(-1), BigDecimal.valueOf(12)));

        result = BigDecimal.valueOf(-45.03);
        assertEquals(result, decreasedPenaltyFee.calculateBalanceAmountToSet(BigDecimal.valueOf(-33.03), BigDecimal.valueOf(12)));
    }
}