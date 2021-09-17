package com.ironhack.midtermproject.classes;

import com.ironhack.midtermproject.model.Checking;
import com.ironhack.midtermproject.model.Saving;

import java.math.BigDecimal;

public class DecreasedPenaltyFee {

    private static final BigDecimal PENALTY_FEE = BigDecimal.valueOf(40);

    public static void updateAmountSaving(Saving saving) {

        BigDecimal result = new BigDecimal(String.valueOf(saving.getBalance().getAmount()));
        result = result.subtract(PENALTY_FEE);

        Money money = new Money(result);
        saving.setBalance(money);
    }

    public static boolean calculatePenaltyFeeSavingAccounts(Saving saving, BigDecimal amount) {

        boolean penalty = false;

        BigDecimal result = new BigDecimal(String.valueOf(saving.getBalance().getAmount()));
        BigDecimal minimumBalance = saving.getMinimumBalance();
        result = result.subtract(amount);

        switch (result.compareTo(minimumBalance)) {
            case -1:
                penalty = true;
                break;
            case 0:
            case 1:
                break;
        }
        return penalty;
    }

    public static void updateAmountChecking(Checking checking) {

        BigDecimal result = new BigDecimal(String.valueOf(checking.getBalance().getAmount()));
        result = result.subtract(PENALTY_FEE);

        Money money = new Money(result);
        checking.setBalance(money);
    }

    public static boolean calculatePenaltyFeeCheckingAccounts(Checking checking, BigDecimal amount) {

        boolean penalty = false;

        BigDecimal result = new BigDecimal(String.valueOf(checking.getBalance().getAmount()));
        BigDecimal minimumBalance = checking.getMinimumBalance();
        result = result.subtract(amount);

        switch (result.compareTo(minimumBalance)) {
            case -1:
                penalty = true;
                break;
            case 0:
            case 1:
                break;
        }
        return penalty;
    }
}
