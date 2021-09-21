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
    void updateAmountSaving_UpdateAmount_SavingAccount() {

        decreasedPenaltyFee.updateAmountSaving(saving, saving.getPenaltyFee());
        assertEquals(BigDecimal.valueOf(960.00).setScale(2, RoundingMode.HALF_EVEN), saving.getBalance().getAmount());
        decreasedPenaltyFee.updateAmountSaving(saving, saving.getPenaltyFee());
        assertEquals(BigDecimal.valueOf(920.00).setScale(2, RoundingMode.HALF_EVEN), saving.getBalance().getAmount());
        decreasedPenaltyFee.updateAmountSaving(saving, saving.getPenaltyFee());
        assertEquals(BigDecimal.valueOf(880.00).setScale(2, RoundingMode.HALF_EVEN), saving.getBalance().getAmount());
    }

    @Test
    void calculatePenaltyFeeSavingAccounts_ReturnFalse_NotUnderMinimumBalance() {

        assertFalse(decreasedPenaltyFee.isPenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(1)));
        assertFalse(decreasedPenaltyFee.isPenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(10)));
        assertFalse(decreasedPenaltyFee.isPenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(300)));
        assertFalse(decreasedPenaltyFee.isPenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(600)));
    }


    @Test
    void calculatePenaltyFeeSavingAccounts_ReturnTrue_UnderMinimumBalance() {

        assertTrue(decreasedPenaltyFee.isPenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(901)));
        assertTrue(decreasedPenaltyFee.isPenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(910)));
        assertTrue(decreasedPenaltyFee.isPenaltyFeeSavingAccounts(saving, BigDecimal.valueOf(999)));
    }

    @Test
    void updateAmountChecking_UpdateAmount_CheckingAccount() {

        decreasedPenaltyFee.updateAmountChecking(checking, checking.getPenaltyFee());
        assertEquals(BigDecimal.valueOf(960.00).setScale(2, RoundingMode.HALF_EVEN), checking.getBalance().getAmount());
        decreasedPenaltyFee.updateAmountChecking(checking, checking.getPenaltyFee());
        assertEquals(BigDecimal.valueOf(920.00).setScale(2, RoundingMode.HALF_EVEN), checking.getBalance().getAmount());
        decreasedPenaltyFee.updateAmountChecking(checking, checking.getPenaltyFee());
        assertEquals(BigDecimal.valueOf(880.00).setScale(2, RoundingMode.HALF_EVEN), checking.getBalance().getAmount());
    }

    @Test
    void calculatePenaltyFeeCheckingAccounts_ReturnFalse_NotUnderMinimumBalance() {

        assertFalse(decreasedPenaltyFee.isPenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(1)));
        assertFalse(decreasedPenaltyFee.isPenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(10)));
        assertFalse(decreasedPenaltyFee.isPenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(300)));
        assertFalse(decreasedPenaltyFee.isPenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(600)));
    }


    @Test
    void calculatePenaltyFeeCheckingAccounts_ReturnTrue_UnderMinimumBalance() {

        assertTrue(decreasedPenaltyFee.isPenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(751)));
        assertTrue(decreasedPenaltyFee.isPenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(910)));
        assertTrue(decreasedPenaltyFee.isPenaltyFeeCheckingAccounts(checking, BigDecimal.valueOf(999)));
    }
}