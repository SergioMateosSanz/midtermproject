package com.ironhack.midtermproject.classes;

import com.ironhack.midtermproject.enums.AccountStatus;
import com.ironhack.midtermproject.model.Checking;
import com.ironhack.midtermproject.model.Saving;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class DecreasedPenaltyFeeTest {

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
    void updateAmountSaving_UpdateAmount_SavingAccount() {

        DecreasedPenaltyFee.updateAmountSaving(saving);
        assertEquals(BigDecimal.valueOf(960.00).setScale(2, RoundingMode.HALF_EVEN), saving.getBalance().getAmount());
        DecreasedPenaltyFee.updateAmountSaving(saving);
        assertEquals(BigDecimal.valueOf(920.00).setScale(2, RoundingMode.HALF_EVEN), saving.getBalance().getAmount());
        DecreasedPenaltyFee.updateAmountSaving(saving);
        assertEquals(BigDecimal.valueOf(880.00).setScale(2, RoundingMode.HALF_EVEN), saving.getBalance().getAmount());
    }

    @Test
    void calculatePenaltyFeeSavingAccounts_ReturnFalse_NotUnderMinimumBalance() {

        assertFalse(DecreasedPenaltyFee.calculatePenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(1)));
        assertFalse(DecreasedPenaltyFee.calculatePenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(10)));
        assertFalse(DecreasedPenaltyFee.calculatePenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(300)));
        assertFalse(DecreasedPenaltyFee.calculatePenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(600)));
    }


    @Test
    void calculatePenaltyFeeSavingAccounts_ReturnTrue_UnderMinimumBalance() {

        assertTrue(DecreasedPenaltyFee.calculatePenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(901)));
        assertTrue(DecreasedPenaltyFee.calculatePenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(910)));
        assertTrue(DecreasedPenaltyFee.calculatePenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(999)));
    }

    @Test
    void updateAmountChecking_UpdateAmount_CheckingAccount() {

        DecreasedPenaltyFee.updateAmountChecking(checking);
        assertEquals(BigDecimal.valueOf(960.00).setScale(2, RoundingMode.HALF_EVEN), checking.getBalance().getAmount());
        DecreasedPenaltyFee.updateAmountChecking(checking);
        assertEquals(BigDecimal.valueOf(920.00).setScale(2, RoundingMode.HALF_EVEN), checking.getBalance().getAmount());
        DecreasedPenaltyFee.updateAmountChecking(checking);
        assertEquals(BigDecimal.valueOf(880.00).setScale(2, RoundingMode.HALF_EVEN), checking.getBalance().getAmount());
    }

    @Test
    void calculatePenaltyFeeCheckingAccounts_ReturnFalse_NotUnderMinimumBalance() {

        assertFalse(DecreasedPenaltyFee.calculatePenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(1)));
        assertFalse(DecreasedPenaltyFee.calculatePenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(10)));
        assertFalse(DecreasedPenaltyFee.calculatePenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(300)));
        assertFalse(DecreasedPenaltyFee.calculatePenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(600)));
    }


    @Test
    void calculatePenaltyFeeCheckingAccounts_ReturnTrue_UnderMinimumBalance() {

        assertTrue(DecreasedPenaltyFee.calculatePenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(751)));
        assertTrue(DecreasedPenaltyFee.calculatePenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(910)));
        assertTrue(DecreasedPenaltyFee.calculatePenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(999)));
    }
}